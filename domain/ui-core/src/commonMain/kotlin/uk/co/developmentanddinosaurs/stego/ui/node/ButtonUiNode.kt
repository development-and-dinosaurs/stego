package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

/**
 * UI model for a button.
 *
 * @property id A unique identifier for this component.
 * @property text The text to display on the button.
 * @property onClick The interaction to be invoked when the button is clicked.
 */
@StegoNode(type = "stego.button")
data class ButtonUiNode(
    override val id: String,
    val text: String,
    val onClick: ButtonAction,
) : UiNode
