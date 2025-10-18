package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.LogicState
import uk.co.developmentanddinosaurs.stego.statemachine.State

@Serializable
data class LogicStateDto(
    val id: String,
    val onEntry: List<ActionDto> = emptyList(),
    val onExit: List<ActionDto> = emptyList(),
    val on: Map<String, List<TransitionDto>> = emptyMap(),
    val invoke: InvokableDto? = null,
    val initial: String? = null,
    val states: Map<String, LogicStateDto> = emptyMap()
) {
    fun toDomain(): State = LogicState(
        id = id,
        onEntry = listOf(),
        onExit = listOf(),
        on = on.mapValues { (_, transitions) -> transitions.map { it.toDomain() } },
        invoke = null,
        initial = initial,
        states = states.mapValues { (_, stateDto) -> stateDto.toDomain() }
    )
}
