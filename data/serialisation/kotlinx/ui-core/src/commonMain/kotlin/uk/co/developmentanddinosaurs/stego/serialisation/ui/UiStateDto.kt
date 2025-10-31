package uk.co.developmentanddinosaurs.stego.serialisation.ui

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.serialisation.ActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.InvokableDefinitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.serialisation.TransitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto

@Serializable
@SerialName("ui")
data class UiStateDto(
    override val id: String,
    override val initial: String? = null,
    override val invoke: InvokableDefinitionDto? = null,
    override val on: Map<String, List<TransitionDto>> = emptyMap(),
    override val onEntry: List<ActionDto> = emptyList(),
    override val onExit: List<ActionDto> = emptyList(),
    override val states: Map<String, StateDto> = emptyMap(),
    val uiNode: UiNodeDto,
) : StateDto
