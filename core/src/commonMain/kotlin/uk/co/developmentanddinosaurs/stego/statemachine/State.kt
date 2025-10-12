package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a state within the state machine.
 *
 * A state can be simple (a leaf state) or hierarchical (a parent state that contains substates).
 * It defines actions to be performed on entry and exit, and a set of transitions that can be triggered by events.
 *
 * @property id A unique identifier for the state within its parent's scope.
 * @property onEntry A list of [Action]s to be executed when the state machine enters this state.
 * @property onExit A list of [Action]s to be executed when the state machine exits this state.
 * @property on A map where keys are event types and values are a list of possible [Transition]s.
 * @property invoke An optional [Invokable] service to be executed upon entering this state.
 * @property initial The ID of the initial substate. Required if this is a hierarchical parent state.
 * @property states An optional map of substates, keyed by their IDs.
 */
data class State(
    val id: String,
    val onEntry: List<Action> = emptyList(),
    val onExit: List<Action> = emptyList(),
    val on: Map<String, List<Transition>> = emptyMap(),
    val invoke: Invokable? = null,
    val initial: String? = null,
    val states: Map<String, State> = emptyMap(),
)
