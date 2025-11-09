package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.annotations.StegoNode
import uk.co.developmentanddinosaurs.stego.ui.UserInteraction
import uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule

/**
 * UI model for a text field.
 *
 * @property id A unique identifier for this component.
 * @property label The text to display in the label.
 * @property text The initial text to display in the field.
 * @property onTextChanged The interaction to be invoked when the text changes.
 * @property validators A list of validation rules to apply to the text field.
 */
@StegoNode(type = "stego.text_field")
data class TextFieldUiNode(
  override val id: String,
  val label: String,
  val text: String,
  val onTextChanged: UserInteraction,
  val validators: List<ValidationRule> = emptyList(),
) : UiNode
