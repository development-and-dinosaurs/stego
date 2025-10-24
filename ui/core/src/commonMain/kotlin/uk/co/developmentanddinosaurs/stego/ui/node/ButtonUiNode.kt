package uk.co.developmentanddinosaurs.stego.ui.node

/**
 * UI model for a button.
 *
 * @property id A unique identifier for this component.
 * @property text The text to display on the button.
 * @property onClick The interaction to be invoked when the button is clicked.
 */
data class ButtonUiNode(
    override val id: String,
    val text: String,
    val onClick: UserInteraction,
) : UiNode
