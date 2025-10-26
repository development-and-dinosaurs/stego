package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.TransitionDto
import uk.co.developmentanddinosaurs.stego.statemachine.Transition
import uk.co.developmentanddinosaurs.stego.statemachine.guards.Guard

/**
 * Maps a [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.TransitionDto] to a [uk.co.developmentanddinosaurs.stego.statemachine.Transition] domain object.
 */
class TransitionMapper(private val actionMapper: ActionDtoMapper) {
    fun map(dto: TransitionDto): Transition {
        return Transition(
            target = dto.target,
            actions = dto.actions.map { actionMapper.map(it) },
            guard = dto.guard?.let { Guard.Companion.create(it) }
        )
    }
}
