package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.State

interface StateDto {
    val id: String
    val initial: String?
    val invoke: InvokableDefinitionDto?
    val on: Map<String, List<TransitionDto>>
    val onEntry: List<ActionDto>
    val onExit: List<ActionDto>
    val states: Map<String, StateDto>

    fun toDomain(): State
}
