package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.LogicState
import uk.co.developmentanddinosaurs.stego.statemachine.State

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
): StateDto {
    override fun toDomain(): State = LogicState(
        id = id,
        onEntry = listOf(),
        onExit = listOf(),
        on = on.mapValues { (_, transitions) -> transitions.map { it.toDomain() } },
        invoke = null,
        initial = initial,
        states = states.mapValues { (_, stateDto) -> stateDto.toDomain() }
    )
}
