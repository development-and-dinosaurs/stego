package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LogicStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.StateDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.StateMachineDefinitionDto
import uk.co.developmentanddinosaurs.stego.statemachine.LogicState
import uk.co.developmentanddinosaurs.stego.statemachine.State
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineDefinition
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException
import kotlin.reflect.KClass

/**
 * Maps state machine DTOs from the core module to domain objects.
 *
 * This mapper is designed to be extensible. It can handle core DTO types and delegate
 * to other mappers for types it doesn't recognize (like UiStateDto).
 */
class StateMapper(
    private val invokableMapper: InvokableDefinitionMapper,
    private val actionMapper: ActionMapper,
    private val transitionMapper: TransitionMapper,
    private val extensionMappers: Map<KClass<out StateDto>, (StateDto) -> State> = emptyMap()
) {
    fun map(dto: StateMachineDefinitionDto): StateMachineDefinition {
        return StateMachineDefinition(
            initial = dto.initial,
            states = dto.states.mapValues { (_, stateDto) -> map(stateDto) },
            initialContext = dto.initialContext
        )
    }

    fun map(dto: StateDto): State {
        val extensionMapper = extensionMappers[dto::class]
        if (extensionMapper != null) {
            return extensionMapper(dto)
        }

        return when (dto) {
            is LogicStateDto -> map(dto)
            else -> throw StateMachineException("No mapper found for StateDto type: ${dto::class.simpleName}")
        }
    }

    fun map(dto: LogicStateDto): LogicState {
        return LogicState(
            id = dto.id,
            onEntry = dto.onEntry.map { actionMapper.map(it) },
            onExit = dto.onExit.map { actionMapper.map(it) },
            on = dto.on.mapValues { (_, transitions) -> transitions.map { transitionMapper.map(it) } },
            invoke = dto.invoke?.let { invokableMapper.map(it) },
            initial = dto.initial,
            states = dto.states.mapValues { (_, stateDto) -> map(stateDto) }
        )
    }
}
