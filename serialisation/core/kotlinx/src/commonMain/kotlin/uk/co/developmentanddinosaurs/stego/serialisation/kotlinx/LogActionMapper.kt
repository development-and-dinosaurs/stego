package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.LogAction
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException

/**
 * Maps a [LogActionDto] to a [LogAction] domain object.
 */
class LogActionMapper(private val logger: (String) -> Unit) : ActionDtoMapper {
    override fun map(dto: ActionDto): Action {
        val logActionDto = dto as? LogActionDto
            ?: throw StateMachineException("LogActionMapper can only map LogActionDto")
        return LogAction(logActionDto.message, logger)
    }
}
