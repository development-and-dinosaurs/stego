package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException
import kotlin.reflect.KClass

/**
 * An [ActionDtoMapper] that uses a registry to map custom action DTOs and also handles built-in types.
 *
 * @param actionRegistry A map where keys are DTO types and values are the mapping functions to the corresponding [Action] implementations.
 */
class RegistryActionMapper(private val actionRegistry: Map<KClass<out ActionDto>, (ActionDto) -> Action>) : ActionDtoMapper {

    /**
     * Performs the mapping from DTO to domain object.
     */
    override fun map(dto: ActionDto): Action {
        println("Mapping action")
        // First, check for built-in types
        return when (dto) {
            is AssignActionDto -> dto.toDomain()
            is LogActionDto -> dto.toDomain()
            // Then, consult the registry for custom types
            else -> actionRegistry[dto::class]?.invoke(dto)
                ?: throw StateMachineException("Action DTO type '${dto::class.simpleName}' not found in registry and is not a built-in type.")
        }
    }
}
