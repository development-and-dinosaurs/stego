package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.State
import uk.co.developmentanddinosaurs.stego.ui.UiState

@Serializable
data class UiStateDto(
    val id: String,
    val onEntry: List<ActionDto> = emptyList(),
    val onExit: List<ActionDto> = emptyList(),
    val on: Map<String, List<TransitionDto>> = emptyMap(),
    val invoke: InvokableDto? = null,
    val initial: String? = null,
    val states: Map<String, UiStateDto> = emptyMap(),
    val view: ViewDto
) {
    fun toDomain(): State = UiState(
        id = id,
        onEntry = emptyList(),
        onExit = emptyList(),
        on = on.mapValues { (_, transitions) -> transitions.map { it.toDomain() } },
        invoke = null,
        initial = initial,
        states = states.mapValues { (_, stateDto) -> stateDto.toDomain() },
        view = view.toDomain()
    )
}
