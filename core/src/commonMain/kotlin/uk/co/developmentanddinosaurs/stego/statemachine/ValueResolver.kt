package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * A utility object for parsing and resolving values within the state machine.
 *
 * This component is responsible for two key tasks:
 * 1.  **Parsing**: Converting a string representation (e.g., from JSON) into the appropriate
 *     [DataValue], which could be a literal primitive or a [ValueReference].
 * 2.  **Resolving**: Taking a [DataValue] that might be a [ValueReference] and resolving it
 *     against the current state machine context and event to get a concrete value.
 */
object ValueResolver {
    /**
     * Resolves a [DataValue] that may be a [ValueReference] into a concrete value.
     *
     * If the input value is a `ValueReference`, it will be resolved against the context and event.
     * Otherwise, the value is returned as-is.
     *
     * @param value The [DataValue] to resolve.
     * @param context The current state machine context.
     * @param event The current event being processed.
     * @return The resolved, concrete [DataValue].
     * @throws StateMachineException if a `ValueReference` cannot be resolved.
     */
    fun resolve(
        value: DataValue,
        context: Context,
        event: Event,
    ): DataValue {
        return (value as? ValueReference)?.resolve(context, event) ?: value
    }

    /**
     * Parses a string into the most appropriate [DataValue].
     *
     * This function inspects the string content to determine if it's a reference to the
     * context or event, or if it's a literal string.
     *
     * @param value The string to parse.
     * @return The corresponding [DataValue] (e.g., [ContextReference], [EventReference], or [StringPrimitive]).
     */
    fun parse(value: String): DataValue {
        return when {
            value.startsWith("context.") -> ContextReference(value.substringAfter("context."))
            value.startsWith("event.") -> EventReference(value.substringAfter("event."))
            else -> StringPrimitive(value)
        }
    }
}