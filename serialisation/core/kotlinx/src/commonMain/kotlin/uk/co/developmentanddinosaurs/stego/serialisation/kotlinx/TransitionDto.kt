package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.Transition
import uk.co.developmentanddinosaurs.stego.statemachine.guards.Guard

@Serializable
data class TransitionDto(
    val target: String,
    val actions: List<ActionDto> = emptyList(),
    val guard: String? = null
) {
    fun toDomain(): Transition = Transition(
        target = target,
        actions = listOf(),
        guard = if(guard == null) null else Guard.create(guard)
    )
}
