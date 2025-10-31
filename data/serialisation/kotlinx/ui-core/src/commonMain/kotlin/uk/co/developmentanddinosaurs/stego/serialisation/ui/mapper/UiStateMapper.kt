package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.ActionDtoMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.InvokableDefinitionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.StateDtoMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.TransitionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.statemachine.State
import uk.co.developmentanddinosaurs.stego.ui.UiState

/**
 * Maps a [uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto] to a [uk.co.developmentanddinosaurs.stego.ui.UiState] domain object.
 *
 * This mapper orchestrates the mapping of the state itself and its nested properties.
 * It relies on a [uk.co.developmentanddinosaurs.stego.serialisation.mappers.CompositeStateMapper] to handle nested states, breaking potential circular dependencies.
 */
class UiStateMapper(
    private val stateMapper: StateDtoMapper,
    private val actionMapper: ActionDtoMapper,
    private val invokableMapper: InvokableDefinitionMapper,
    private val transitionMapper: TransitionMapper,
    private val uiNodeMapper: UiNodeMapper,
) : StateDtoMapper {
    override fun map(dto: StateDto): State {
        if (dto !is UiStateDto) {
            throw IllegalArgumentException("Invalid dto of type ${dto::class}!")
        }
        return UiState(
            id = dto.id,
            onEntry = dto.onEntry.map { actionMapper.map(it) },
            onExit = dto.onExit.map { actionMapper.map(it) },
            on = dto.on.mapValues { (_, transitions) -> transitions.map { transitionMapper.map(it) } },
            invoke = dto.invoke?.let { invokableMapper.map(it) },
            initial = dto.initial,
            states = dto.states.mapValues { (_, stateDto) -> stateMapper.map(stateDto) },
            uiNode = uiNodeMapper.map(dto.uiNode),
        )
    }
}