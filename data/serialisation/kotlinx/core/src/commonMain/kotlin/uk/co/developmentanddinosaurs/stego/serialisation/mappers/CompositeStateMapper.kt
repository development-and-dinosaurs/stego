package uk.co.developmentanddinosaurs.stego.serialisation.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.serialisation.StateMachineDefinitionDto
import uk.co.developmentanddinosaurs.stego.statemachine.State
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineDefinition
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException
import kotlin.reflect.KClass

/**
 * The primary entry point for mapping a complete [StateMachineDefinitionDto] into its
 * domain [StateMachineDefinition] counterpart.
 *
 * This class acts as a composite mapper, holding a registry of specific [StateDtoMapper]
 * implementations. It uses a factory pattern for creating these mappers, allowing them
 * to be aware of the composite mapper itself. This is crucial for recursively mapping
 * hierarchical state machines, where a child state's mapper needs to delegate back
 * to the main composite mapper to map its own children.
 */
class CompositeStateMapper(
    mapperFactories: Map<KClass<out StateDto>, (StateDtoMapper) -> StateDtoMapper>,
) : StateDtoMapper {
    private val mapperMap: Map<KClass<out StateDto>, StateDtoMapper> =
        mapperFactories.mapValues { (_, factory) -> factory(this) }

    /**
     * Maps a top-level [StateMachineDefinitionDto] to a [StateMachineDefinition].
     *
     * This function handles the mapping of the initial state, the initial context, and then
     * delegates the mapping of each individual state DTO to the appropriate registered mapper.
     *
     * @param dto The complete state machine definition DTO to map.
     * @return The resulting domain [StateMachineDefinition].
     * @throws StateMachineException if any part of the mapping fails, such as an unmappable
     *   initial context value or an unregistered state DTO type.
     */
    fun map(dto: StateMachineDefinitionDto): StateMachineDefinition {
        val initialContext =
            dto.initialContext.mapValues { (_, valueDto) ->
                valueDto.toDomain()
                    ?: throw StateMachineException("Failed to map initial context value: $valueDto")
            }
        return StateMachineDefinition(
            initial = dto.initial,
            states = dto.states.mapValues { (_, stateDto) -> map(stateDto) },
            initialContext = initialContext,
        )
    }

    /**
     * Maps a single [StateDto] to its domain [State] counterpart by delegating to the
     * appropriate mapper from the registry.
     *
     * @param dto The state DTO to map.
     * @return The resulting domain [State].
     * @throws StateMachineException if no mapper is found for the given DTO type.
     */
    override fun map(dto: StateDto): State {
        val mapper =
            mapperMap[dto::class]
                ?: throw StateMachineException("Unsupported StateDto type: ${dto::class.simpleName}")
        return mapper.map(dto)
    }
}
