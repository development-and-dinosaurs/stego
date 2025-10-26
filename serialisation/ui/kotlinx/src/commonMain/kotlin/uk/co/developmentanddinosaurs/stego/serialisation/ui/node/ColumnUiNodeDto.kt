package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("column")
data class ColumnUiNodeDto(
    override val id: String,
    val children: List<UiNodeDto>,
) : UiNodeDto
