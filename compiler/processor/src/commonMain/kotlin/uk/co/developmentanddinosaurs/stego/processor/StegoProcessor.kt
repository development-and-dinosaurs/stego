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
import com.google.devtools.ksp.symbol.Variance
import kotlinx.serialization.json.Json
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
        val nodeSymbols =
            resolver
                .getSymbolsWithAnnotation(StegoNode::class.qualifiedName!!)
                .filterIsInstance<KSClassDeclaration>()

        logger.info("Found ${nodeSymbols.count()} annotated classes")
        if (!nodeSymbols.iterator().hasNext()) {
            return emptyList()
        }

        writeMetadata(nodeSymbols)

        return emptyList()
    }

    private fun writeMetadata(nodeSymbols: Sequence<KSClassDeclaration>) {
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

    private fun toNodeInfo(classDeclaration: KSClassDeclaration): NodeInfo {
        val stegoAnnotation =
            classDeclaration.annotations.first { it.shortName.asString() == StegoNode::class.simpleName }
        val typeArgument =
            stegoAnnotation.arguments.first { it.name?.asString() == "type" }

        val properties =
            classDeclaration
                .getAllProperties()
                .map { property ->
                    PropertyInfo(
                        name = property.simpleName.asString(),
                        typeQualifiedName = property.type.resolve().toTypeName(),
                    )
                }.toList()

        return NodeInfo(
            qualifiedName = classDeclaration.qualifiedName!!.asString(),
            simpleName = classDeclaration.simpleName.asString(),
            type = typeArgument.value as String,
            properties = properties,
        )
    }

    private fun KSType.toTypeName(): String {
        val baseType = this.declaration.qualifiedName?.asString() ?: return ""
        if (this.arguments.isEmpty()) {
            return baseType
        }
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
