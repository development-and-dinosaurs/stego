package uk.co.developmentanddinosaurs.stego.serialisation.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.LogicStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.statemachine.LogicState
import uk.co.developmentanddinosaurs.stego.statemachine.State

/**
 * A specific mapper responsible for converting a [LogicStateDto] into a [LogicState].
 *
 * This mapper handles the conversion of all properties of a state, including its
 * entry/exit actions and any nested states.
 *
 * @param actionMapper The mapper to use for converting action DTOs.
 * @param invokableMapper The mapper to use for converting invokable definition DTOs.
 * @param transitionMapper The mapper to use for converting transition DTOs.
 * @param compositeStateMapper The top-level mapper to delegate back to for mapping nested states.
 */
class LogicStateMapper(
    private val actionMapper: ActionMapper,
    private val invokableMapper: InvokableDefinitionMapper,
    private val transitionMapper: TransitionMapper,
    private val compositeStateMapper: StateDtoMapper,
) : StateDtoMapper {
    override fun map(dto: StateDto): State {
        require(dto is LogicStateDto) { "LogicStateMapper can only map LogicStateDto" }
        return LogicState(
            id = dto.id,
            initial = dto.initial,
            invoke = dto.invoke?.let { invokableMapper.map(it) },
            on = dto.on.mapValues { (_, transitions) -> transitions.map { transitionMapper.map(it) } },
            onEntry = dto.onEntry.map { actionMapper.map(it) },
            onExit = dto.onExit.map { actionMapper.map(it) },
            states = dto.states.mapValues { (_, stateDto) -> compositeStateMapper.map(stateDto) },
        )
    }
}
