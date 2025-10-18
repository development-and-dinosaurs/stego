package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.LogAction

class LogActionMapper(private val logger: (String) -> Unit) : ActionMapper {
    override fun canMap(actionDto: ActionDto): Boolean {
        return actionDto is LogActionDto
    }

    override fun map(actionDto: ActionDto): Action {
        require(actionDto is LogActionDto) { "ActionDto must be a LogActionDto" }
        return LogAction(actionDto.message, logger)
    }
}
