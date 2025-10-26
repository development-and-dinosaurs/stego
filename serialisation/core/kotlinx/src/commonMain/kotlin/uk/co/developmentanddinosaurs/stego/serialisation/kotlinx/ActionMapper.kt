package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException
import kotlin.reflect.KClass

/**
 * Maps serializable [ActionDto] objects to their corresponding domain [Action] implementations.
 *
 * This class acts as a bridge between the data transfer layer and the core state machine logic.
 * It has built-in support for standard actions like [AssignActionDto] and [LogActionDto].
 * For any other custom actions, it consults a provided registry.
 *
 * Built-in actions always take precedence over the registry.
 *
 * @param actionRegistry A map for registering custom action types. The key should be the
 *   [KClass] of the custom DTO, and the value should be a lambda that maps the DTO to its
 *   corresponding domain [Action].
 *   Example:
 *   ```
 *   val registry = mapOf(
 *       CustomActionDto::class to { dto -> (dto as CustomActionDto).toDomain() }
 *   )
 *   val mapper = ActionMapper(registry)
 *   ```
 */
class ActionMapper(private val actionRegistry: Map<KClass<out ActionDto>, (ActionDto) -> Action>) {
    /**
     * Performs the mapping from a single [ActionDto] to its domain [Action] counterpart.
     *
     * @param dto The data transfer object to map.
     * @return The corresponding domain [Action] object.
     * @throws StateMachineException if the DTO type is not a built-in type and is not found
     *   in the `actionRegistry`.
     */
    fun map(dto: ActionDto): Action {
        return when (dto) {
            is AssignActionDto -> dto.toDomain()
            is LogActionDto -> dto.toDomain()
            else -> actionRegistry[dto::class]?.invoke(dto)
                ?: throw StateMachineException("Action DTO type '${dto::class.simpleName}' not found in registry and is not a built-in type.")
        }
    }
}