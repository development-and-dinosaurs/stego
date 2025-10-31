package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("image")
data class ImageUiNodeDto(
    override val id: String,
    val url: String,
    val contentDescription: String,
) : UiNodeDto
