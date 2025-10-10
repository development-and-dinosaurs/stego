package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a state within the state machine.
 *
 * A state can be simple (a leaf state) or hierarchical (a parent state that contains substates).
 * It defines actions to be performed on entry and exit, and a set of transitions that can be triggered by events.
 */
interface State {
    /**
     * A unique identifier for the state within its parent's scope.
     */
    val id: String

    /**
     * A list of [Action]s to be executed when the state machine enters this state.
     * For hierarchical states, parent onEntry actions are executed before child onEntry actions.
     */
    val onEntry: List<Action>

    /**
     * A list of [Action]s to be executed when the state machine exits this state.
     * For hierarchical states, child onExit actions are executed before parent onExit actions.
     */
    val onExit: List<Action>

    /**
     * A map where keys are event types (e.g., "SUBMIT") and values are a list of possible [Transition]s.
     * When an event occurs, the machine looks up the event type in this map to find potential transitions.
     */
    val on: Map<String, List<Transition>>

    /**
     * An optional [Invokable] service to be executed upon entering this state.
     * If present, the state machine will pause and wait for the invoked service to complete or error.
     */
    val invoke: Invokable?

    /**
     * The ID of the initial substate. This is required if the state is a hierarchical parent (i.e., [states] is not empty).
     * When transitioning to this parent state, the machine will automatically enter this initial substate.
     */
    val initial: String?

    /**
     * An optional map of substates, keyed by their IDs.
     * If this map is not null or empty, this state is a hierarchical (or compound) state.
     */
    val states: Map<String, State>?
}
