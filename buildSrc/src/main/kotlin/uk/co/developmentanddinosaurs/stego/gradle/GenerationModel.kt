package uk.co.developmentanddinosaurs.stego.gradle

import kotlinx.serialization.Serializable

@Serializable
internal data class NodeMetadata(
    val qualifiedName: String,
    val simpleName: String,
    val type: String,
    val properties: List<PropertyMetadata>,
    val superType: String?,
)

@Serializable
internal data class PropertyMetadata(
    val name: String,
    val typeQualifiedName: String
)

internal fun mapToDto(typeString: String): String {
    val typeMappings = mapOf(
        "uk.co.developmentanddinosaurs.stego.ui.node.UiNode" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto",
        "uk.co.developmentanddinosaurs.stego.ui.node.ButtonAction" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonActionDto",
        "uk.co.developmentanddinosaurs.stego.ui.node.UserInteraction" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UserInteractionDto",
        "uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule" to "uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto",
    )
    return typeMappings[typeString] ?: if (typeString.startsWith("uk.co.developmentanddinosaurs.stego.ui")) typeString + "Dto" else typeString
}

internal fun getDtoPackage(qualifiedName: String): String {
    val basePackage = qualifiedName.substringBeforeLast('.').replace(".ui.", ".serialisation.ui.")
    return basePackage.replace(".node", ".node").replace(".validators", ".validators")
}
