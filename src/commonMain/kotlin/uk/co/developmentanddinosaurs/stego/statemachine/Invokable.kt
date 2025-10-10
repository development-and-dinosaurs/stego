package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a long-running service or another process that can be invoked by a [State].
 *
 * When a state machine enters a state with an `invoke` block, it will execute the specified service.
 * Upon completion, the invoked service is expected to send an event back to the state machine to trigger the next transition.
 * This promotes a clean, unified event-driven architecture.
 *
 * @property src The source or identifier of the invokable content.
 * @property onDone The [Event] to be sent when the invoked service completes successfully.
 * @property onError The [Event] to be sent when the invoked service reports an error.
 */
data class Invokable(
    val src: String,
    val onDone: Event,
    val onError: Event
)
