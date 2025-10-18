package uk.co.developmentanddinosaurs.stego.serialisation.ui

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.InvokableDefinitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.TransitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto

@Serializable
data class UiStateDto(
    val id: String,
    val initial: String? = null,
    val invoke: InvokableDefinitionDto? = null,
    val on: Map<String, List<TransitionDto>> = emptyMap(),
    val onEntry: List<ActionDto> = emptyList(),
    val onExit: List<ActionDto> = emptyList(),
    val states: Map<String, UiStateDto> = emptyMap(),
    val uiNode: UiNodeDto
)
