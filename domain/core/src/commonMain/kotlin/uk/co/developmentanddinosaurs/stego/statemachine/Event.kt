package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents an event that can be sent to the state machine to trigger transitions.
 *
 * Events are the primary means of external communication with the state machine.
 *
 * @property type A string identifier that uniquely defines the type of the event (e.g.,
 *   "SUBMIT_FORM").
 * @property data A map containing payload data associated with the event.
 */
data class Event(
    val type: String,
    val data: Map<String, Any?> = emptyMap(),
)
