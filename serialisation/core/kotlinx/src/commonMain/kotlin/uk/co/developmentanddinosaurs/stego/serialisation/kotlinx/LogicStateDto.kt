package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("logic")
data class LogicStateDto(
    override val id: String,
    override val onEntry: List<ActionDto> = emptyList(),
    override val onExit: List<ActionDto> = emptyList(),
    override val on: Map<String, List<TransitionDto>> = emptyMap(),
    override val invoke: InvokableDefinitionDto? = null,
    override val initial: String? = null,
    override val states: Map<String, LogicStateDto> = emptyMap()
): StateDto