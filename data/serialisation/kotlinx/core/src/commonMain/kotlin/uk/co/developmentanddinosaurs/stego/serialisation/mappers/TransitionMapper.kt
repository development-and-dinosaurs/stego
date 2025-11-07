package uk.co.developmentanddinosaurs.stego.serialisation.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.TransitionDto
import uk.co.developmentanddinosaurs.stego.statemachine.Transition
import uk.co.developmentanddinosaurs.stego.statemachine.guards.Guard

/** Maps a [TransitionDto] to a [Transition] domain object. */
class TransitionMapper(
    private val actionMapper: ActionDtoMapper,
) {
  fun map(dto: TransitionDto): Transition =
      Transition(
          target = dto.target,
          actions = dto.actions.map { actionMapper.map(it) },
          guard = dto.guard?.let { Guard.create(it) },
      )
}
