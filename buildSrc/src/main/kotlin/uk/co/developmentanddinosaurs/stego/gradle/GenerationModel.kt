package uk.co.developmentanddinosaurs.stego.gradle

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
internal data class ComponentMetadata(
    val qualifiedName: String,
    val simpleName: String = qualifiedName.substringAfterLast('.'),
    val packageName: String = qualifiedName.substringBeforeLast('.'),
    val stegoType: String?,
    val properties: List<PropertyMetadata>,
    val superType: String?,
)

@Serializable
internal data class PropertyMetadata(
    val name: String,
    val typeQualifiedName: String
)

@Serializable
data class BaseComponentMetadata(
    val qualifiedName: String,
    val packageName: String = qualifiedName.substringBeforeLast('.'),
    val simpleName: String = qualifiedName.substringAfterLast('.'),
    val properties: List<String>,
)

private val json = Json { ignoreUnknownKeys = true }

internal fun mapToDto(typeString: String): String {
    return if (typeString.contains("stego")) typeString.replace("ui", "serialisation.ui") + "Dto" else typeString
}

internal fun getComponentMetadata(input: File): List<ComponentMetadata> {
    return json.decodeFromString<List<ComponentMetadata>>(input.readText())
}

internal fun getBaseComponentMetadata(input: File): List<BaseComponentMetadata> {
    return json.decodeFromString<List<BaseComponentMetadata>>(input.readText())
}

internal fun parseTypeName(typeString: String): TypeName {
    val trimmedTypeString = typeString.trim()

    val isNullable = trimmedTypeString.endsWith('?')
    val nonNullableTypeString = trimmedTypeString.removeSuffix("?")

    val genericStartIndex = nonNullableTypeString.indexOf('<')
    if (genericStartIndex == -1) {
        return ClassName.bestGuess(mapToDto(nonNullableTypeString)).copy(nullable = isNullable)
    }
    val genericEndIndex = nonNullableTypeString.lastIndexOf('>')
    if (genericEndIndex == -1 || genericEndIndex < genericStartIndex) {
        throw IllegalArgumentException("Invalid type string: Mismatched generic brackets in '$typeString'")
    }
    val rawTypeString = nonNullableTypeString.take(genericStartIndex)
    val rawClassName = ClassName.bestGuess(rawTypeString)
    val argsString = nonNullableTypeString.substring(genericStartIndex + 1, genericEndIndex)

    if (argsString.isBlank()) {
        return rawClassName.copy(nullable = isNullable)
    }

    val typeArgs = mutableListOf<TypeName>()
    var nestLevel = 0
    var lastSplit = 0

    argsString.forEachIndexed { index, char ->
        if (char == '<') nestLevel++
        if (char == '>') nestLevel--
        if (char == ',' && nestLevel == 0) {
            val typeArgString = argsString.substring(lastSplit, index).trim()
            if (typeArgString.isNotEmpty()) {
                typeArgs.add(parseTypeName(typeArgString))
            }
            lastSplit = index + 1
        }
    }
    val lastTypeArgString = argsString.substring(lastSplit).trim()
    if (lastTypeArgString.isNotEmpty()) {
        typeArgs.add(parseTypeName(lastTypeArgString))
    }
    return rawClassName.parameterizedBy(typeArgs).copy(nullable = isNullable)
}
