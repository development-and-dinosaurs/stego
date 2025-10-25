package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("label")
data class LabelUiNodeDto(
    override val id: String,
    val text: String,
    ) : UiNodeDto
