package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.LogicState
import uk.co.developmentanddinosaurs.stego.statemachine.State

/**
 * Maps a [LogicStateDto] to a [LogicState] domain object.
 */
class LogicStateMapper(
    private val stateMapper: StateDtoMapper,
    private val invokableMapper: InvokableDefinitionMapper,
    private val transitionMapper: TransitionMapper,
    private val actionMapper: ActionDtoMapper
) : StateDtoMapper {
    override fun map(dto: StateDto): State {
        val logicStateDto = dto as LogicStateDto
        return LogicState(
            id = logicStateDto.id,
            onEntry = logicStateDto.onEntry.map { actionMapper.map(it) },
            onExit = logicStateDto.onExit.map { actionMapper.map(it) },
            on = dto.on.mapValues { (_, transitions) -> transitions.map { transitionMapper.map(it) } },
            invoke = logicStateDto.invoke?.let { invokableMapper.map(it) },
            initial = logicStateDto.initial,
            states = logicStateDto.states.mapValues { (_, stateDto) -> stateMapper.map(stateDto) }
        )
    }
}
