package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * An action that logs a given message using a provided logger implementation.
 *
 * This action is useful for debugging and tracing the state machine's execution flow.
 * The actual logging mechanism is injected via the `logger` lambda, making this action
 * platform-agnostic.
 *
 * @property message The static string message to be logged.
 * @property logger The platform-specific logging implementation that will be called with the message.
 */
class LogAction(
    private val message: String,
    private val logger: (String) -> Unit,
) : Action {
    /**
     * Executes the log action by passing the configured message to the logger.
     *
     * This action does not modify the state machine's context.
     *
     * @param context The current state machine context (unused).
     * @param event The triggering event (unused).
     * @return The original, unmodified context.
     */
    override fun execute(
        context: Context,
        event: Event,
    ): Context {
        logger("LogAction: $message")
        return context
    }
}
