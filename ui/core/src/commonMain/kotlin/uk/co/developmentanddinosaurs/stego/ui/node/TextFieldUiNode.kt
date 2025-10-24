package uk.co.developmentanddinosaurs.stego.ui.node

/**
 * UI model for a text field.
 *
 * @property id A unique identifier for this component.
 * @property label The text to display in the label.
 * @property text The initial text to display in the field.
 * @property onTextChanged The interaction to be invoked when the text changes.
 */
data class TextFieldUiNode(
    override val id: String,
    val label: String,
    val text: String,
    val onTextChanged: UserInteraction,
) : UiNode