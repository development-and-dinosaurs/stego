package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("text_field")
data class TextFieldUiNodeDto(
    override val id: String,
    val text: String,
    val label: String,
    val onTextChanged: InteractionDto,
    ) : UiNodeDto
