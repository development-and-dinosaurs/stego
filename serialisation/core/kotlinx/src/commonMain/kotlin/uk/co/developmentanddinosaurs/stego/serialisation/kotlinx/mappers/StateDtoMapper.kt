package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.StateDto
import uk.co.developmentanddinosaurs.stego.statemachine.State

fun interface StateDtoMapper {
    fun map(dto: StateDto): State
}
