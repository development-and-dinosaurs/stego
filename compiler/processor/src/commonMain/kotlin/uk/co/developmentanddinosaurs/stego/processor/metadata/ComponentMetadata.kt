package uk.co.developmentanddinosaurs.stego.processor.metadata

import kotlinx.serialization.Serializable

/**
 * Holds all the extracted information about a class annotated with @StegoNode.
 * This is designed to be serialized to JSON for consumption by another processor.
 */
@Serializable
data class ComponentMetadata(
    val qualifiedName: String,
    val simpleName: String = qualifiedName.substringAfterLast('.'),
    val stegoType: String?,
    val properties: List<PropertyInfo>,
    val superType: String?,
)

@Serializable
data class PropertyInfo(
    val name: String,
    val typeQualifiedName: String,
)

@Serializable
data class BaseComponentMetadata(
    val qualifiedName: String,
    val properties: List<String>,
)
