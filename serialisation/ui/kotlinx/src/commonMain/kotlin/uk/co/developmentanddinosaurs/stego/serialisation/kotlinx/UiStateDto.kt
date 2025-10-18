package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable

@Serializable
data class UiStateDto(
    val id: String,
    val initial: String? = null,
    val invoke: InvokableDefinitionDto? = null,
    val on: Map<String, List<TransitionDto>> = emptyMap(),
    val onEntry: List<ActionDto> = emptyList(),
    val onExit: List<ActionDto> = emptyList(),
    val states: Map<String, UiStateDto> = emptyMap(),
    val view: ViewDto
)
