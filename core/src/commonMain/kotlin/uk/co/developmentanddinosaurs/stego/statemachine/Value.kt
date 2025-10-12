package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a value that can be sourced from different locations within the state machine.
 */
sealed interface Value<T> {
    /**
     * Retrieves the concrete data for this value from the appropriate source.
     *
     * @param context The current context of the state machine.
     * @param event The current event being processed.
     * @return The resolved value, or null if not found or of the wrong type.
     */
    fun resolve(
        context: Context,
        event: Event,
    ): T?
}

/**
 * Represents a value that is retrieved from the state machine's context.
 */
data class ContextValue<T>(
    val path: String,
) : Value<T> {
    override fun resolve(
        context: Context,
        event: Event,
    ): T = context.get(path)
}

/**
 * Represents a value that is retrieved from the data payload of the triggering [Event].
 */
data class EventValue<T>(
    val path: String,
) : Value<T> {
    @Suppress("UNCHECKED_CAST")
    override fun resolve(
        context: Context,
        event: Event,
    ): T = event.data[path] as T
}

/**
 * Represents a fixed, literal (or constant) value.
 */
data class LiteralValue<T>(
    val value: T,
) : Value<T> {
    override fun resolve(
        context: Context,
        event: Event,
    ): T = value
}
