package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.State

interface StateDtoMapper {
    fun map(dto: StateDto): State
}
