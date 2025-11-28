package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule

/**
 * UI model for a text field.
 *
 * @property id A unique identifier for this component.
 * @property label The text to display in the label.
 * @property text The initial text to display in the field.
 * @property onTextChanged The interaction to be invoked when the text changes.
 * @property validation A list of validation rules to apply to the text field.
 */
data class TextFieldUiNode(
    override val id: String,
    val label: String,
    val text: String,
    val onTextChanged: UserInteraction,
    val validation: List<ValidationRule> = emptyList(),
) : UiNode
