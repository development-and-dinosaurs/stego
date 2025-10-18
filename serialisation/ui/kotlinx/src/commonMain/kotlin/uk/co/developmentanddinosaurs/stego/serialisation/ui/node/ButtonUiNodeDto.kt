package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.EventDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto

@Serializable
@SerialName("button")
data class ButtonUiNodeDto(val text: String, val onClick: EventDto) : UiNodeDto
