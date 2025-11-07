package uk.co.developmentanddinosaurs.stego.serialisation.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.statemachine.State

fun interface StateDtoMapper {
  fun map(dto: StateDto): State
}
