package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action

/**
 * Defines a contract for mapping an [ActionDto] to an [Action] domain object.
 */
interface ActionDtoMapper {
    fun map(dto: ActionDto): Action
}
