package uk.co.developmentanddinosaurs.stego.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.symbol.Variance
import kotlinx.serialization.json.Json
import uk.co.developmentanddinosaurs.stego.annotations.StegoComponent
import uk.co.developmentanddinosaurs.stego.annotations.StegoNode
import uk.co.developmentanddinosaurs.stego.processor.metadata.NodeInfo
import uk.co.developmentanddinosaurs.stego.processor.metadata.PropertyInfo

@OptIn(KspExperimental::class)
class StegoProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private val json = Json

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("Stego processor started")

        val stegoComponents =
            resolver
                .getSymbolsWithAnnotation(StegoComponent::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()

        val stegoNodes =
            resolver
                .getSymbolsWithAnnotation(StegoNode::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()

        val allRootSymbols = (stegoComponents + stegoNodes).distinctBy { it.qualifiedName }

        val allSymbols =
            allRootSymbols
                .flatMap {
                    if (it.modifiers.contains(Modifier.SEALED)) it.getSealedSubclasses() else sequenceOf(it)
                }.distinctBy { it.qualifiedName }

        logger.info("Found ${allSymbols.count()} component classes for metadata generation")
        logger.info("${allSymbols.toList()}")
        if (!allSymbols.iterator().hasNext()) {
            return emptyList()
        }

        writeNodeMetadata(allSymbols)
        writeBaseDtoMetadata(allSymbols)

        return emptyList()
    }

    private fun writeNodeMetadata(nodeSymbols: Sequence<KSClassDeclaration>) {
        val nodes = nodeSymbols.map(::toNodeInfo).toList()
        val nodesJson = json.encodeToString(nodes)

        val outputFile =
            codeGenerator.createNewFile(
                dependencies =
                    Dependencies(
                        aggregating = true,
                        sources = nodeSymbols.mapNotNull { it.containingFile }.toList().toTypedArray(),
                    ),
                packageName = "stego",
                fileName = "nodes",
                extensionName = "json",
            )

        outputFile.write(nodesJson.toByteArray())
    }

    private fun writeBaseDtoMetadata(nodeSymbols: Sequence<KSClassDeclaration>) {
        val nodeQualifiedNames = nodeSymbols.map { it.qualifiedName!!.asString() }.toSet()

        val externalSupertypes =
            nodeSymbols
                .flatMap { it.superTypes }
                .map { it.resolve().declaration }
                .filterIsInstance<KSClassDeclaration>()
                .filterNot { it.qualifiedName!!.asString() in nodeQualifiedNames }
                .filterNot { it.qualifiedName!!.asString().startsWith("kotlin.") }
                .distinctBy { it.qualifiedName }

        if (!externalSupertypes.iterator().hasNext()) return

        val baseDtoProperties =
            externalSupertypes.associate { classDecl ->
                val properties = classDecl.getAllProperties().map { it.simpleName.asString() }.toList()
                classDecl.qualifiedName!!.asString() to properties
            }

        val baseDtosJson = json.encodeToString(baseDtoProperties)

        val outputFile =
            codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = true, sources = emptyArray()),
                packageName = "stego",
                fileName = "base-dtos",
                extensionName = "json",
            )

        outputFile.write(baseDtosJson.toByteArray())
        logger.info("Wrote base DTO metadata for: ${baseDtoProperties.keys}")
    }

    private fun toNodeInfo(classDeclaration: KSClassDeclaration): NodeInfo {
        val stegoComponentAnnotation =
            classDeclaration.annotations.find { it.shortName.asString() == StegoComponent::class.simpleName }
        val stegoNodeAnnotation =
            classDeclaration.annotations.find { it.shortName.asString() == StegoNode::class.simpleName }

        // Prioritize StegoComponent's type, but fall back to StegoNode's for compatibility.
        val type =
            (stegoComponentAnnotation?.arguments?.firstOrNull { it.name?.asString() == "type" }?.value as? String)
                ?.takeIf { it.isNotBlank() }
                ?: (
                    stegoNodeAnnotation?.arguments?.firstOrNull { it.name?.asString() == "type" }?.value as? String
                        ?: ""
                )

        val directSuperType =
            classDeclaration.superTypes
                .map { it.resolve().declaration }
                .filterIsInstance<KSClassDeclaration>()
                .filterNot { it.qualifiedName!!.asString().startsWith("kotlin.") }
                .firstOrNull()
                ?.qualifiedName
                ?.asString()

        val constructorParameters = classDeclaration.primaryConstructor?.parameters ?: emptyList()
        val properties =
            constructorParameters.map { parameter ->
                val resolvedType = parameter.type.resolve()

                PropertyInfo(
                    name = parameter.name!!.asString(),
                    typeQualifiedName = resolvedType.toTypeName(),
                )
            }

        return NodeInfo(
            qualifiedName = classDeclaration.qualifiedName!!.asString(),
            simpleName = classDeclaration.simpleName.asString(),
            type = type,
            properties = properties,
            superType = directSuperType,
        )
    }

    private fun KSType.toTypeName(): String {
        val baseType = this.declaration.qualifiedName?.asString() ?: return "*"
        val typeName =
            if (this.arguments.isEmpty()) {
                baseType
            } else {
                toGenericTypeName(baseType)
            }
        return if (this.nullability == Nullability.NULLABLE) "$typeName?" else typeName
    }

    private fun KSType.toGenericTypeName(baseType: String): String {
        val genericArgs =
            this.arguments.joinToString(separator = ", ") { argument ->
                val type = argument.type?.resolve()
                val variance =
                    when (argument.variance) {
                        Variance.CONTRAVARIANT -> "in "
                        Variance.COVARIANT -> "out "
                        else -> ""
                    }
                variance + (type?.toTypeName() ?: "*")
            }

        return "$baseType<$genericArgs>"
    }
}
