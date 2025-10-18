package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a reference to a value, which can be resolved from different locations.
 */
sealed interface ValueReference : DataValue {
    /**
     * Resolves the reference to a concrete [DataValue].
     *
     * @param context The current context of the state machine.
     * @param event The current event being processed.
     * @return The resolved [DataValue], or null if the reference cannot be found.
     */
    fun resolve(
        context: Context,
        event: Event,
    ): DataValue?
}

/**
 * A reference to a value stored in the state machine's context.
 */
data class ContextReference(
    val path: String,
) : ValueReference {
    override fun resolve(
        context: Context,
        event: Event,
    ): DataValue? {
        val segments = path.split('.')
        var current: DataValue? = context.get(segments.first())

        for (i in 1 until segments.size) {
            current =
                if (current is ObjectValue) {
                    current.value[segments[i]]
                } else {
                    return null
                }
        }
        return current
    }
}

/**
 * A reference to a value stored in the triggering event's data payload.
 */
data class EventReference(
    val path: String,
) : ValueReference {
    override fun resolve(
        context: Context,
        event: Event,
    ): DataValue? {
        val segments = path.split('.')
        var current: DataValue? = event.data[segments.first()]

        for (i in 1 until segments.size) {
            current =
                if (current is ObjectValue) {
                    current.value[segments[i]]
                } else {
                    return null
                }
        }
        return current
    }
}

/**
 * A reference to a fixed, literal (or constant) value.
 */
data class LiteralReference(
    val value: DataValue,
) : ValueReference {
    override fun resolve(
        context: Context,
        event: Event,
    ): DataValue = value
}
