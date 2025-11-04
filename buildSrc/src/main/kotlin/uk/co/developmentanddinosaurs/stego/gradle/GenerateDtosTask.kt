package uk.co.developmentanddinosaurs.stego.gradle

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateDtosTask : DefaultTask() {
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:InputFile
    abstract val baseDtosFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val input = inputFile.get().asFile
        val output = outputDir.get().asFile
        output.deleteRecursively()
        output.mkdirs()

        val json = Json { ignoreUnknownKeys = true }
        val nodes = json.decodeFromString<List<NodeMetadata>>(input.readText())

        val externalSuperTypeProperties =
            baseDtosFile.get().asFile.let {
                if (it.exists()) {
                    json.decodeFromString<Map<String, List<String>>>(it.readText())
                } else {
                    emptyMap()
                }
            }

        val nodesByQualifiedName = nodes.associateBy { it.qualifiedName }
        val superTypeProperties =
            nodes.associate { node -> node.qualifiedName to (nodesByQualifiedName[node.superType]?.properties?.map { p -> p.name }?.toSet() ?: externalSuperTypeProperties[node.superType]?.toSet() ?: emptySet()) }

        nodes.forEach { node ->
            val dtoName = "${node.simpleName}Dto"
            val packageName = getDtoPackage(node.qualifiedName)

            val serializableAnnotation = ClassName("kotlinx.serialization", "Serializable")

            val constructorBuilder = FunSpec.constructorBuilder()
            val propertySpecs = node.properties.map { property ->
                val typeName = parseTypeName(property.typeQualifiedName)

                val parameterSpec = com.squareup.kotlinpoet.ParameterSpec.builder(property.name, typeName)
                if (typeName.isNullable) {
                    parameterSpec.defaultValue("null")
                }
                constructorBuilder.addParameter(parameterSpec.build())

                val propertySpec = PropertySpec.builder(property.name, typeName)
                    .initializer(property.name)

                if (superTypeProperties[node.qualifiedName]?.contains(property.name) == true) {
                    propertySpec.addModifiers(KModifier.OVERRIDE)
                }

                propertySpec.build()
            }

            val dtoClass = TypeSpec.classBuilder(dtoName)
                .addModifiers(KModifier.DATA)
                .addAnnotation(serializableAnnotation)
                .primaryConstructor(constructorBuilder.build())
                .addProperties(propertySpecs)

            if (node.type.isNotBlank()) {
                val serialNameAnnotation = AnnotationSpec.builder(ClassName("kotlinx.serialization", "SerialName"))
                    .addMember("%S", node.type)
                    .build()
                dtoClass.addAnnotation(serialNameAnnotation)
            }

            node.superType?.let {
                println("Super type: $it")
                if (it != "kotlin.Any") {
                    dtoClass.addSuperinterface(ClassName.bestGuess(mapToDto(it)))
                }
            }

            val fileSpec = FileSpec.builder(packageName, dtoName)
                .addType(dtoClass.build())
                .build()

            fileSpec.writeTo(output)
        }
    }

    private fun parseTypeName(typeString: String): TypeName {
        val trimmed = typeString.trim()
        val isNullable = trimmed.endsWith('?')
        val nonNullableTypeString = if (isNullable) trimmed.removeSuffix("?") else trimmed

        if (trimmed == "*") return STAR

        val genericStartIndex = nonNullableTypeString.indexOf('<')
        if (genericStartIndex == -1) {
            return ClassName.bestGuess(mapToDto(nonNullableTypeString)).copy(nullable = isNullable)
        }

        val genericEndIndex = nonNullableTypeString.lastIndexOf('>')
        if (genericEndIndex == -1) return ClassName.bestGuess(nonNullableTypeString)

        val rawTypeString = nonNullableTypeString.take(genericStartIndex)
        val rawClassName = ClassName.bestGuess(mapToDto(rawTypeString))

        val argsString = nonNullableTypeString.substring(genericStartIndex + 1, genericEndIndex)

        val typeArgs = mutableListOf<TypeName>()
        var nestLevel = 0
        var lastSplit = 0

        argsString.forEachIndexed { index, char ->
            if (char == '<') nestLevel++
            if (char == '>') nestLevel--
            if (char == ',' && nestLevel == 0) {
                typeArgs.add(parseTypeName(argsString.substring(lastSplit, index)))
                lastSplit = index + 1
            }
        }
        typeArgs.add(parseTypeName(argsString.substring(lastSplit)))

        return rawClassName.parameterizedBy(typeArgs).copy(nullable = isNullable)
    }
}
