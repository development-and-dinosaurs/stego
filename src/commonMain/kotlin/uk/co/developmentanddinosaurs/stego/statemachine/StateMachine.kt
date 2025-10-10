package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a complete, executable state machine.
 *
 * This interface defines the core engine of the state machine, responsible for managing the current state,
 * holding the extended state [Context], and processing incoming [Event]s.
 */
interface StateMachine {
    /**
     * The ID of the top-level initial [State] that the machine will be in upon starting.
     * This must correspond to a key in the [states] map.
     */
    val initial: String

    /**
     * A map of all top-level states in the state machine, keyed by their unique IDs.
     * For hierarchical states, substates are defined within their parent [State] objects.
     */
    val states: Map<String, State>

    /**
     * The extended state data of the state machine.
     * This [Context] is passed to [Action]s and [Guard]s and is updated immutably.
     */
    val context: Context

    /**
     * The current active [State] of the state machine.
     * This property provides a snapshot of the machine's status at any given time.
     */
    val currentState: State

    /**
     * Sends an event to the state machine to be processed.
     *
     * This is the primary method for interacting with the state machine. The machine will:
     * 1. Find a [Transition] matching the [event] type in the current state (and its parents).
     * 2. Evaluate the transition's [Guard] condition.
     * 3. If the guard passes, execute the transition's [Action]s and move to the target [State].
     *
     * @param event The event to be processed.
     * @return The new current [State] after the event has been processed.
     */
    fun send(event: Event): State
}
