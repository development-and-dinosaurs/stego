package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents an event that can be sent to the state machine to trigger transitions.
 *
 * Events are the primary means of external communication with the state machine.
 */
interface Event {
    /**
     * A string identifier that uniquely defines the type of the event (e.g., "SUBMIT_FORM", "LOGIN_SUCCESS").
     * The state machine uses this type to look up the appropriate [Transition].
     */
    val type: String

    /**
     * A map containing payload data associated with the event.
     * This allows events to carry information that can be used by [Guard]s for conditional transitions
     * or by [Action]s to update the [Context].
     */
    val data: Map<String, Any>
}
