package uk.co.developmentanddinosaurs.stego.ui.node

/**
 * UI model for an image.
 *
 * @property id A unique identifier for this component.
 * @property url The URL of the image to display.
 * @property contentDescription Text used by screen readers to describe what is in the image.
 */
data class ImageUiNode(
    override val id: String,
    val url: String,
    val contentDescription: String
) : UiNode
