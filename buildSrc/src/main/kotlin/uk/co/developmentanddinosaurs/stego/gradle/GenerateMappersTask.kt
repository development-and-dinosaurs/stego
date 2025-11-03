package uk.co.developmentanddinosaurs.stego.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateMappersTask : DefaultTask() {
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    private val componentRootToMapperInterface = mapOf(
        "uk.co.developmentanddinosaurs.stego.ui.node.UiNode" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.UiNodeMapper",
        "uk.co.developmentanddinosaurs.stego.ui.node.ButtonAction" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ButtonActionMapper",
        "uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ValidationRuleMapper",
    )

    @get:Input
    val mapperTypeMappings =
        mapOf(
            "uk.co.developmentanddinosaurs.stego.ui.node.ButtonAction" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ButtonActionMapper",
            "uk.co.developmentanddinosaurs.stego.ui.node.UserInteraction" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.UserInteractionMapper",
            "uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ValidationRuleMapper",
            "uk.co.developmentanddinosaurs.stego.ui.node.UiNode" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.UiNodeMapper",
        )

    @TaskAction
    fun generate() {
        val input = inputFile.get().asFile
        val output = outputDir.get().asFile
        output.deleteRecursively()
        output.mkdirs()

        val json = Json { ignoreUnknownKeys = true }
        val nodes = json.decodeFromString<List<NodeMetadata>>(input.readText())

        nodes.forEach { node ->
            val mapperName = "${node.simpleName}Mapper"
            val basePackage = node.qualifiedName.substringBeforeLast('.').replace(".ui.", ".serialisation.ui.")
            val packageName = basePackage.replace(".node", ".mapper").replace(".validators", ".mapper")

            val nodeClassName = ClassName.bestGuess(node.qualifiedName)
            val dtoPackage = getDtoPackage(node.qualifiedName)
            val dtoClassName = ClassName(dtoPackage, "${node.simpleName}Dto")

            val mapperInterfaceName = componentRootToMapperInterface[node.superType] ?: return@forEach
            val mapperInterface = ClassName.bestGuess(mapperInterfaceName)
            val dtoInterface = ClassName.bestGuess(mapToDto(node.superType!!))
            val domainInterface = ClassName.bestGuess(node.superType!!)

            val constructorBuilder = FunSpec.constructorBuilder()
            val constructorMappers = mutableMapOf<String, String>()
            val propertySpecs = mutableListOf<PropertySpec>()

            val mappingStatements =
                node.properties.joinToString(",\n") { property ->
                    val propertyAssignment =
                        if (mapperTypeMappings.containsKey(property.typeQualifiedName.substringBefore('<')) ||
                            (property.typeQualifiedName.contains("<") && mapperTypeMappings.containsKey(property.typeQualifiedName.substringAfter('<').substringBefore('>')))
                        ) {
                            val typeToMap = if (property.typeQualifiedName.contains("<")) property.typeQualifiedName.substringAfter('<').substringBefore('>') else property.typeQualifiedName.substringBefore('<')
                            val mapperFqName = mapperTypeMappings.getValue(typeToMap)
                            val mapperClassName = ClassName.bestGuess(mapperFqName)
                            val mapperFieldName = mapperClassName.simpleName.replaceFirstChar { it.lowercase() }
                            if (!constructorMappers.containsKey(mapperFqName)) {
                                constructorBuilder.addParameter(mapperFieldName, mapperClassName)
                                propertySpecs.add(
                                    PropertySpec.builder(mapperFieldName, mapperClassName, KModifier.PRIVATE)
                                        .initializer(mapperFieldName)
                                        .build()
                                )
                                constructorMappers[mapperFqName] = mapperFieldName
                            }
                            val isCollection = property.typeQualifiedName.startsWith("kotlin.collections.List<")
                            if (isCollection) {
                                "${property.name} = dto.${property.name}.map { ${constructorMappers[mapperFqName]!!}.map(it) }"
                            } else {
                                "${property.name} = ${constructorMappers[mapperFqName]!!}.map(dto.${property.name})"
                            }
                        } else {
                            "${property.name} = dto.${property.name}"
                        }
                    "    $propertyAssignment"
                }

            val primaryConstructor = constructorBuilder.build()

            val mapFunction =
                FunSpec.builder("map")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("dto", dtoInterface)
                    .returns(domainInterface)
                    .addStatement("require(dto is %T)", dtoClassName)
                    .addStatement("return %T(\n%L\n)", nodeClassName, mappingStatements)
                    .build()

            val mapperClass =
                TypeSpec.classBuilder(mapperName)
                    .addSuperinterface(mapperInterface)
                    .primaryConstructor(primaryConstructor)
                    .addProperties(propertySpecs)
                    .addFunction(mapFunction)
                    .build()

            val fileSpec = FileSpec.builder(packageName, mapperName).addType(mapperClass).build()
            fileSpec.writeTo(output)
        }
    }
}