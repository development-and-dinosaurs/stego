package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("progress_indicator")
class ProgressIndicatorUiNodeDto(
    override val id: String,
) : UiNodeDto
