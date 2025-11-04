package uk.co.developmentanddinosaurs.stego.processor.metadata

import kotlinx.serialization.Serializable

/**
 * Holds all the extracted information about a class annotated with @StegoNode.
 * This is designed to be serialized to JSON for consumption by another processor.
 */
@Serializable
data class NodeInfo(
    val qualifiedName: String,
    val simpleName: String,
    val type: String,
    val properties: List<PropertyInfo>,
    val superType: String?,
)

@Serializable
data class PropertyInfo(
    val name: String,
    val typeQualifiedName: String,
)
