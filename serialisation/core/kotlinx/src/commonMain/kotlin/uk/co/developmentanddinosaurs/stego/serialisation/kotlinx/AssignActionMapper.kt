package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException

class AssignActionMapper : ActionDtoMapper {
    override fun map(dto: ActionDto): Action {
        val assignActionDto = dto as? AssignActionDto
            ?: throw StateMachineException("AssignActionMapper can only map AssignActionDto")
        return assignActionDto.toDomain()
    }
}
