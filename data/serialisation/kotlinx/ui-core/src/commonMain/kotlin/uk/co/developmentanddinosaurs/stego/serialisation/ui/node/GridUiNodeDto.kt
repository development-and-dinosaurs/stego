package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("grid")
data class GridUiNodeDto(
    override val id: String,
    val columns: Int,
    val children: List<UiNodeDto>,
) : UiNodeDto
