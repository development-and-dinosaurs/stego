package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.EventDto

@Serializable
@SerialName("text_field")
data class TextFieldUiNodeDto(val text: String, val label: String, val onTextChanged: EventDto) : UiNodeDto
