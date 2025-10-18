package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.Transition

@Serializable
data class TransitionDto(
    val target: String,
    val actions: List<ActionDto> = emptyList(),
    val guard: GuardDto? = null
) {
    fun toDomain(): Transition = Transition(
        target = target,
        actions = listOf(),
        guard = guard?.toDomain()
    )
}
