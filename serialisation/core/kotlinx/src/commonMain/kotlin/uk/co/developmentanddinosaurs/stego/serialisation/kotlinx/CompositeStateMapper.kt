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
    private val mapperMap: Map<KClass<out StateDto>, StateDtoMapper>
) : StateDtoMapper {

    fun map(dto: StateMachineDefinitionDto): StateMachineDefinition {
        println("Mapping state machine")
        return StateMachineDefinition(
            initial = dto.initial,
            states = dto.states.mapValues { (_, stateDto) -> map(stateDto) },
            initialContext = dto.initialContext
        )
    }

    override fun map(dto: StateDto): State {
        println("Mapping state $dto")
        val mapper = mapperMap[dto::class]
            ?: throw StateMachineException("Unsupported StateDto type: ${dto::class.simpleName}")
        return mapper.map(dto)
    }
}