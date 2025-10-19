package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.State
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineDefinition
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException
import kotlin.reflect.KClass

/**
 * A composite [StateDtoMapper] that holds a map of other mappers.
 * It looks up the correct mapper based on the DTO's class and delegates the mapping task.
 */
class CompositeStateMapper(
    mapperFactories: Map<KClass<out StateDto>, (StateDtoMapper) -> StateDtoMapper>
) : StateDtoMapper {
    private val mapperMap: Map<KClass<out StateDto>, StateDtoMapper> =
        mapperFactories.mapValues { (_, factory) -> factory(this) }

    fun map(dto: StateMachineDefinitionDto): StateMachineDefinition {
        val initialContext = dto.initialContext.mapValues { (_, valueDto) ->
            valueDto.toDomain()
                ?: throw StateMachineException("Failed to map initial context value: $valueDto")
        }
        return StateMachineDefinition(
            initial = dto.initial,
            states = dto.states.mapValues { (_, stateDto) -> map(stateDto) },
            initialContext = initialContext
        )
    }

    override fun map(dto: StateDto): State {
        val mapper = mapperMap[dto::class]
            ?: throw StateMachineException("Unsupported StateDto type: ${dto::class.simpleName}")
        return mapper.map(dto)
    }
}
