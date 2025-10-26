package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.AssignActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LogActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers.ActionMapper.Companion.defaultMappers
import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException
import kotlin.reflect.KClass

/**
 * Maps serializable [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ActionDto] objects to their corresponding domain [Action] implementations
 * using a configurable registry of mappers.
 *
 * This class acts as a bridge between the data transfer layer and the core state machine logic.
 * It is configured with a registry that maps DTO types to their specific [ActionDtoMapper].
 *
 * To use, provide a map of any custom mappers. These will be merged with the
 * [defaultMappers]. Providing a custom mapper for a default type will override it.
 *
 *   Example for adding a new custom action:
 *   ```
 *   // This mapper will support AssignAction, LogAction, and CustomAction.
 *   val mapper = ActionMapper(mapOf(
 *       CustomActionDto::class to CustomActionMapper()
 *   ))
 *   ```
 *
 *   Example for overriding a default action (e.g., for platform-specific logging):
 *   ```
 *   // On Android, you might do this to use Android's Logcat.
 *   val androidLogMapper = LogActionMapper { message -> Log.d("StateMachine", message) }
 *   val mapper = ActionMapper(mapOf(
 *       LogActionDto::class to androidLogMapper
 *   ))
 *   ```
 *
 * @param customMappers A map of custom mappers to be added to the default set. If a key
 *   in this map conflicts with a default mapper, the custom mapper will be used.
 */
class ActionMapper(
    customMappers: Map<KClass<out ActionDto>, ActionDtoMapper> = emptyMap(),
) {
    private val mappers: Map<KClass<out ActionDto>, ActionDtoMapper> = defaultMappers + customMappers

    companion object {
        /**
         * A map containing the default, built-in action mappers provided by the library.
         */
        val defaultMappers: Map<KClass<out ActionDto>, ActionDtoMapper> =
            mapOf(
                AssignActionDto::class to AssignActionMapper(),
                LogActionDto::class to LogActionMapper(),
            )
    }

    /**
     * Performs the mapping from a single [ActionDto] to its domain [Action] counterpart.
     *
     * @param dto The data transfer object to map.
     * @return The corresponding domain [Action] object.
     * @throws StateMachineException if the DTO type is not a built-in type and is not found
     *   in the provided `mappers` registry.
     */
    fun map(dto: ActionDto): Action {
        val mapper =
            mappers[dto::class]
                ?: throw StateMachineException("Action DTO type '${dto::class.simpleName}' not found in mapper registry.")
        return mapper.map(dto)
    }
}
