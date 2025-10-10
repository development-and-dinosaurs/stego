package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a long-running service or another process that can be invoked by a [State].
 *
 * When a state machine enters a state with an `invoke` block, it will execute the specified service.
 * Upon completion, the invoked service is expected to send an event back to the state machine to trigger the next transition.
 * This promotes a clean, unified event-driven architecture.
 */
interface Invokable {
    /**
     * The source or identifier of the invokable content.
     * This could be a URL for a web service, an ID for another state machine, or any other service locator.
     */
    val src: String

    /**
     * The event type to be sent when the invoked service completes successfully.
     * The service's result data should be included in the [Event.data] payload.
     */
    val onDone: Event

    /**
     * The event type to be sent when the invoked service reports an error.
     * The error details should be included in the [Event.data] payload.
     */
    val onError: Event
}
