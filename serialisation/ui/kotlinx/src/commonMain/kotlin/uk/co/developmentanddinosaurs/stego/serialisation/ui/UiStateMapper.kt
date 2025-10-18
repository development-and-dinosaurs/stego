package uk.co.developmentanddinosaurs.stego.serialisation.ui

import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.InvokableDefinitionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.UiNodeMapper
import uk.co.developmentanddinosaurs.stego.statemachine.State
import uk.co.developmentanddinosaurs.stego.ui.UiState

/**
 * Maps a [UiStateDto] to a [UiState] domain object.
 *
 * This mapper orchestrates the mapping of the state itself, as well as its nested
 * actions, transitions, and invokable services.
 */
class UiStateMapper(
    private val actionMapper: ActionMapper,
    private val invokableMapper: InvokableDefinitionMapper,
    private val uiNodeMapper: UiNodeMapper,
) {
    fun map(dto: UiStateDto): State =
        UiState(
            id = dto.id,
            onEntry = dto.onEntry.map { actionMapper.map(it) },
            onExit = dto.onExit.map { actionMapper.map(it) },
            on = dto.on.mapValues { (_, transitions) -> transitions.map { it.toDomain() } },
            invoke = dto.invoke?.let { invokableMapper.map(it) },
            initial = dto.initial,
            states = dto.states.mapValues { (_, stateDto) -> this.map(stateDto) },
            uiNode = uiNodeMapper.map(dto.uiNode),
        )
}