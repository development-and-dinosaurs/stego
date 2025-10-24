package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("button")
data class ButtonUiNodeDto(
    override val id: String,
    val text: String,
    val onClick: InteractionDto,
) : UiNodeDto
