package uk.co.developmentanddinosaurs.stego.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateMappersTask : DefaultTask() {
  @get:InputFile
  abstract val componentsFileProperty: RegularFileProperty

  @get:InputFile
  abstract val baseComponentsFileProperty: RegularFileProperty

  @get:InputFile
  abstract val nodesFileProperty: RegularFileProperty

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  private val serialisationPackage = "uk.co.developmentanddinosaurs.stego.serialisation.ui"
  private val mapperPackage = "$serialisationPackage.mapper"

  private lateinit var rootTypeToMapperInterface: Map<String, ClassName>

  @TaskAction
  fun generate() {
    val baseComponentsFile = baseComponentsFileProperty.get().asFile
    val outputDirectory = outputDir.get().asFile
    outputDirectory.mkdirs()

    rootTypeToMapperInterface = createMapperInterfaces(baseComponentsFile)

    generateNodeMappers(outputDirectory)
    generateComponentMappers(outputDirectory)
  }

  private fun generateNodeMappers(outputDirectory: File) {
    val nodesFile = nodesFileProperty.get().asFile
    val nodes = getComponentMetadata(nodesFile)
    nodes.forEach { node ->
      val mapperSpec = generateNodeMapper(node)
      mapperSpec.writeTo(outputDirectory)
    }
  }

  private fun generateComponentMappers(outputDirectory: File) {
    val componentsFile = componentsFileProperty.get().asFile
    val components = getComponentMetadata(componentsFile)
    components.forEach { components ->
      val mapperSpec = generateComponentMapper(components)
      mapperSpec.writeTo(outputDirectory)
    }
  }

  private fun generateComponentMapper(component: ComponentMetadata): FileSpec {
    val mapperName = "${component.simpleName}Mapper"

    val mapperClass = TypeSpec.classBuilder(mapperName)
      .primaryConstructor(buildNodeConstructor(component))
      .addFunction(buildComponentMapFunction(component))

    component.properties
      .mapNotNull { buildProperty(it) }
      .forEach { mapperClass.addProperty(it) }

    return FileSpec.builder(mapperPackage, mapperName)
      .addType(mapperClass.build())
      .build()
  }

  private fun buildComponentMapFunction(component: ComponentMetadata): FunSpec {
    val dtoClassName = ClassName.bestGuess(mapToDto(component.qualifiedName))

    val mapFunction = FunSpec.builder("map")
      .returns(ClassName.bestGuess(component.qualifiedName))
      .addParameter("dto", dtoClassName)

    val mappings = component.properties.joinToString(",\n") { property ->
      buildMapStatement(property)
    }
    mapFunction.addStatement("return %T(\n%L\n)", ClassName.bestGuess(component.qualifiedName), mappings)
    return mapFunction.build()
  }

  private fun generateNodeMapper(node: ComponentMetadata): FileSpec {
    val mapperName = "${node.simpleName}Mapper"

    val mapperClass = TypeSpec.classBuilder(mapperName)
      .addSuperinterface(ClassName(mapperPackage, "UiNodeMapper"))
      .primaryConstructor(buildNodeConstructor(node))
      .addFunction(buildSupportedTypeFunction(node.stegoType))
      .addFunction(buildNodeMapFunction(node))

    node.properties
      .mapNotNull { buildProperty(it) }
      .forEach { mapperClass.addProperty(it) }

    return FileSpec.builder(mapperPackage, mapperName)
      .addType(mapperClass.build())
      .build()
  }

  private fun buildNodeConstructor(node: ComponentMetadata): FunSpec {
    val constructor = FunSpec.constructorBuilder()
    node.properties.forEach { property ->
      val typeName = parseTypeName(property.typeQualifiedName)
      val propType = property.typeQualifiedName
      if (propType.contains("stego") && !propType.contains("UiNode")) {
        var mapperClassName: ClassName
        var mapperFieldName: String
        if (typeName is ParameterizedTypeName) {
          val actualType = ClassName.bestGuess(typeName.typeArguments[0].toString())
          mapperClassName = ClassName(mapperPackage, actualType.simpleName.replace("Dto", "Mapper"))
          mapperFieldName = mapperClassName.simpleName.replaceFirstChar { it.lowercase() }
        } else {
          val actualType = ClassName.bestGuess(typeName.toString())
          mapperClassName = ClassName(mapperPackage, actualType.simpleName.replace("Dto", "Mapper"))
          mapperFieldName = mapperClassName.simpleName.replaceFirstChar { it.lowercase() }
        }
        constructor.addParameter(mapperFieldName, mapperClassName)
      }

    }
    return constructor.build()
  }

  private fun buildProperty(property: PropertyMetadata): PropertySpec? {
    val mapperClassName = buildMapperClassName(property) ?: return null
    val mapperFieldName = mapperClassName.simpleName.replaceFirstChar { it.lowercase() }
    return PropertySpec.builder(mapperFieldName, mapperClassName, KModifier.PRIVATE)
      .initializer(mapperFieldName)
      .build()
  }

  private fun buildMapperClassName(property: PropertyMetadata): ClassName? {
    val typeName = parseTypeName(property.typeQualifiedName)
    val propType = property.typeQualifiedName
    if (propType.contains("stego") && !propType.contains("UiNode")) {
      if (typeName is ParameterizedTypeName) {
        val actualType = ClassName.bestGuess(typeName.typeArguments[0].toString())
        return ClassName(mapperPackage, actualType.simpleName.replace("Dto", "Mapper"))
      } else {
        val actualType = ClassName.bestGuess(typeName.toString())
        return ClassName(mapperPackage, actualType.simpleName.replace("Dto", "Mapper"))
      }
    }
    return null
  }


  private fun buildNodeMapFunction(node: ComponentMetadata): FunSpec {
    val dtoClassName = ClassName.bestGuess(mapToDto(node.qualifiedName))

    val mapFunction = FunSpec.builder("map")
      .addModifiers(KModifier.OVERRIDE)
      .returns(ClassName.bestGuess("uk.co.developmentanddinosaurs.stego.ui.node.UiNode"))
      .addParameter("dto", ClassName.bestGuess("uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto"))
      .addParameter(
        "registry", ClassName(mapperPackage, "UiNodeMapperRegistry")
      )
      .addStatement("require(dto is %T)", dtoClassName)

    val mappings = node.properties.joinToString(",\n") { property ->
      buildMapStatement(property)
    }
    mapFunction.addStatement("return %T(\n%L\n)", ClassName.bestGuess(node.qualifiedName), mappings)
    return mapFunction.build()
  }

  private fun buildMapStatement(property: PropertyMetadata): String {
    val typeName = parseTypeName(property.typeQualifiedName)
    if (typeName is ParameterizedTypeName) {
      if (property.typeQualifiedName.contains("stego") && !property.typeQualifiedName.contains("ui.node")) {
        val mapperClassName = ClassName.bestGuess(typeName.typeArguments[0].toString().replace("Dto", "Mapper"))
        val mapperFieldName = mapperClassName.simpleName.replaceFirstChar { it.lowercase() }
        return "${property.name} = dto.${property.name}.map { $mapperFieldName.map(it) }"
      }
      return "${property.name} = dto.${property.name}.map { registry.map(it) }"
    }
    if (property.typeQualifiedName.contains("stego") && property.typeQualifiedName.contains("ui.node")) {
      return "${property.name} = registry.map(dto.${property.name})"
    }
    if (property.typeQualifiedName.contains("stego")) {
      val mapperClassName = ClassName.bestGuess(mapToDto(property.typeQualifiedName).replace("Dto", "Mapper"))
      val mapperFieldName = mapperClassName.simpleName.replaceFirstChar { it.lowercase() }
      return "${property.name} = $mapperFieldName.map(dto.${property.name})"
    }
    return "${property.name} = dto.${property.name}"
  }

  private fun createMapperInterfaces(baseComponentsFile: File): Map<String, ClassName> {
    val baseComponents = getBaseComponentMetadata(baseComponentsFile)
    return baseComponents.associate {
      val mapperInterfaceName = "${it.simpleName}Mapper"
      it.qualifiedName to ClassName(mapperPackage, mapperInterfaceName)
    }
  }

  private fun buildSupportedTypeFunction(stegoType: String): FunSpec {
    return FunSpec.builder("supportedType")
      .addModifiers(KModifier.OVERRIDE)
      .returns(String::class)
      .addStatement("return %S", stegoType)
      .build()
  }

}
