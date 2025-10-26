package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.AssignActionDto
import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.AssignAction
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException

/**
 * A specific mapper responsible for converting an [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.AssignActionDto] into an [AssignAction].
 *
 * This mapper is designed for a single purpose and will fail if provided with any DTO
 * other than [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.AssignActionDto]. It is typically used as part of a larger mapping strategy,
 * for example, within a custom action registry provided to [ActionMapper].
 */
class AssignActionMapper : ActionDtoMapper {
    /**
     * Maps an [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.AssignActionDto] to its domain [AssignAction] counterpart.
     *
     * @param dto The data transfer object to map. Must be an instance of [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.AssignActionDto].
     * @return The corresponding domain [AssignAction] object.
     * @throws StateMachineException if the provided `dto` is not an [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.AssignActionDto].
     */
    override fun map(dto: ActionDto): Action {
        require(dto is AssignActionDto) { "AssignActionMapper can only map AssignActionDto" }
        return AssignAction(dto.key, dto.value.toDomain())
    }
}
