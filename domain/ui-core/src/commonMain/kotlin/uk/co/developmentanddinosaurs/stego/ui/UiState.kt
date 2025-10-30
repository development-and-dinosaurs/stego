package uk.co.developmentanddinosaurs.stego.ui

import uk.co.developmentanddinosaurs.stego.statemachine.*
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode

/**
 * A state that contains UI information.
 *
 * @property id A unique identifier for the state within its parent's scope.
 * @property initial The ID of the initial substate. Required if this is a hierarchical parent state.
 * @property invoke An optional [Invokable] service to be executed upon entering this state.
 * @property on A map where keys are event types and values are a list of possible [Transition]s.
 * @property onEntry A list of [Action]s to be executed when the state machine enters this state.
 * @property onExit A list of [Action]s to be executed when the state machine exits this state.
 * @property states An optional map of substates, keyed by their IDs.
 * @property uiNode The UI node to be rendered for this state.
 */
data class UiState(
    override val id: String,
    override val initial: String? = null,
    override val invoke: InvokableDefinition? = null,
    override val on: Map<String, List<Transition>> = emptyMap(),
    override val onEntry: List<Action> = emptyList(),
    override val onExit: List<Action> = emptyList(),
    override val states: Map<String, State> = emptyMap(),
    val uiNode: UiNode,
) : State
