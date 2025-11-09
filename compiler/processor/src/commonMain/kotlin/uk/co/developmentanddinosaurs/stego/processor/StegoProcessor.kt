package uk.co.developmentanddinosaurs.stego.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Nullability
import kotlinx.serialization.json.Json
import uk.co.developmentanddinosaurs.stego.annotations.StegoComponent
import uk.co.developmentanddinosaurs.stego.annotations.StegoNode
import uk.co.developmentanddinosaurs.stego.processor.metadata.BaseComponentMetadata
import uk.co.developmentanddinosaurs.stego.processor.metadata.ComponentMetadata
import uk.co.developmentanddinosaurs.stego.processor.metadata.PropertyInfo

@OptIn(KspExperimental::class)
class StegoProcessor(
  private val codeGenerator: CodeGenerator,
  private val logger: KSPLogger,
) : SymbolProcessor {
  private val json = Json

  override fun process(resolver: Resolver): List<KSAnnotated> {
    logger.info("Stego processor started")
    val symbols = getSymbolsToProcess(resolver)

    if (symbols.isEmpty()) {
      return emptyList()
    }

    val stegoNodes =
      resolver
        .getSymbolsWithAnnotation(StegoNode::class.qualifiedName!!)
        .filterIsInstance<KSClassDeclaration>()
        .toSet()

    val stegoComponents =
      resolver
        .getSymbolsWithAnnotation(StegoComponent::class.qualifiedName!!)
        .filterIsInstance<KSClassDeclaration>()
        .toSet()

    writeStegoNodes(stegoNodes)
    writeStegoComponents(stegoComponents)
    writeBaseComponentMetadata(symbols)

    logger.info("Stego processor finished")
    return emptyList()
  }

  private fun getSymbolsToProcess(resolver: Resolver): Set<KSClassDeclaration> =
    (resolver.getSymbolsWithAnnotation(StegoComponent::class.qualifiedName!!) +
      resolver.getSymbolsWithAnnotation(StegoNode::class.qualifiedName!!))
      .filterIsInstance<KSClassDeclaration>()
      .toSet()

  private fun writeStegoNodes(symbols: Set<KSClassDeclaration>) {
    val stegoNodes = symbols.map(::toComponentMetadata)
    val stegoNodesJson = json.encodeToString(stegoNodes)

    writeJsonFile(symbols, stegoNodesJson, "nodes")
  }

  private fun writeStegoComponents(symbols: Set<KSClassDeclaration>) {
    val components = symbols.map(::toComponentMetadata)
    val componentsJson = json.encodeToString(components)

    writeJsonFile(symbols, componentsJson, "components")
  }

  private fun writeBaseComponentMetadata(symbols: Set<KSClassDeclaration>) {
    val externalSupertypes = getExternalSupertypes(symbols)
    val baseComponents = externalSupertypes.map(::toBaseComponentMetadata)
    val baseComponentsJson = json.encodeToString(baseComponents.toList())

    writeJsonFile(symbols, baseComponentsJson, "base-components")
  }

  private fun writeJsonFile(
    symbols: Set<KSClassDeclaration>,
    stegoNodesJson: String,
    fileName: String,
  ) {
    val outputFile =
      codeGenerator.createNewFile(
        dependencies =
          Dependencies(
            aggregating = true,
            sources = symbols.mapNotNull { it.containingFile }.toList().toTypedArray(),
          ),
        packageName = "stego",
        fileName = fileName,
        extensionName = "json",
      )

    outputFile.write(stegoNodesJson.toByteArray())
  }

  private fun getExternalSupertypes(symbols: Set<KSClassDeclaration>): List<KSClassDeclaration> =
    symbols
      .flatMap { it.superTypes }
      .map { it.resolve().declaration }
      .filterIsInstance<KSClassDeclaration>()
      .filterNot { it.qualifiedName!!.asString().startsWith("kotlin.") }
      .distinctBy { it.qualifiedName }

  private fun toBaseComponentMetadata(declaration: KSClassDeclaration): BaseComponentMetadata =
    BaseComponentMetadata(
      qualifiedName = declaration.qualifiedName!!.asString(),
      properties =
        declaration
          .getAllProperties()
          .map(KSPropertyDeclaration::simpleName)
          .map(KSName::asString)
          .toList(),
    )

  private fun toComponentMetadata(classDeclaration: KSClassDeclaration): ComponentMetadata {
    val stegoAnnotation =
      classDeclaration.annotations.first { it.shortName.asString().contains("Stego") }
    val stegoType =
      stegoAnnotation.arguments.first { it.name?.asString() == "type" }.value as String
    val superType =
      classDeclaration.superTypes
        .map { it.resolve().declaration.qualifiedName?.asString() }
        .filterNotNull()
        .filterNot { it.startsWith("kotlin.") }
        .firstOrNull()
    println()
    println(stegoType)
    classDeclaration.primaryConstructor?.parameters?.forEach {
      it.type.resolve().arguments.forEach {
        println((it.type?.resolve()?.declaration as KSClassDeclaration).superTypes.toList())
      }
    }
    val constructorParameters = classDeclaration.primaryConstructor?.parameters ?: emptyList()
    val properties =
      constructorParameters.map { parameter ->
        val isDecoratedNode = parameter.type.resolve().arguments.any { arg ->
          (arg.type?.resolve()?.declaration as KSClassDeclaration).superTypes.toList().map { it.resolve().declaration.simpleName.asString() }.contains("DecoratedUiNode")
        }
        PropertyInfo(
          name = parameter.name!!.asString(),
          typeQualifiedName = parameter.type.resolve().toTypeName(),
          isDecoratedNode = isDecoratedNode,
        )
      }
    return ComponentMetadata(
      qualifiedName = classDeclaration.qualifiedName!!.asString(),
      stegoType = stegoType.ifBlank { null },
      properties = properties,
      superType = superType,
    )
  }

  private fun KSType.toTypeName(): String {
    val baseType = this.declaration.qualifiedName!!.asString()
    val typeName =
      if (this.arguments.isEmpty()) {
        baseType
      } else {
        this.toGenericTypeName(baseType)
      }
    return if (this.nullability == Nullability.NULLABLE) "$typeName?" else typeName
  }

  private fun KSType.toGenericTypeName(baseType: String): String {
    val genericArgs =
      this.arguments.joinToString(separator = ", ") { argument ->
        argument.type!!.resolve().toTypeName()
      }
    return "$baseType<$genericArgs>"
  }
}
