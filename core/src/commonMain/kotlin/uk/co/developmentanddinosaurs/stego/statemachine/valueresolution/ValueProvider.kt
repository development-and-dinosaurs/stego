package uk.co.developmentanddinosaurs.stego.statemachine.valueresolution

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

/**
 * An interface for providing a value, either from the context or as a literal.
 */
interface ValueProvider {
    fun get(
        context: Context,
        event: Event?,
    ): Any?

    companion object {
        private val DYNAMIC_VALUE_REGEX = Regex("\\{(context|event)\\.(.+?)\\}")

        /**
         * Resolves the input into a specific [ValueProvider].
         * If the value is a String in the format `{context.key}` or `{event.key}`,
         * a corresponding dynamic value provider is created.
         * For other strings, it attempts to convert them to numbers or booleans.
         * Otherwise, a [LiteralValue] is created.
         */
        fun resolve(value: Any?): ValueProvider {
            if (value is String) {
                val match = DYNAMIC_VALUE_REGEX.matchEntire(value)
                if (match != null) {
                    val source = match.groupValues[1]
                    val key = match.groupValues[2]
                    return when (source) {
                        "context" -> ContextValue(key)
                        "event" -> EventValue(key)
                        else -> error("Unknown value source: $source") // Should not happen
                    }
                }

                return when {
                    value.toIntOrNull() != null -> LiteralValue(value.toInt())
                    value.toDoubleOrNull() != null -> LiteralValue(value.toDouble())
                    value.equals("true", ignoreCase = true) -> LiteralValue(true)
                    value.equals("false", ignoreCase = true) -> LiteralValue(false)
                    value.startsWith("\"") && value.endsWith("\"") -> LiteralValue(value.substring(1, value.length - 1))
                    else -> LiteralValue(value)
                }
            }
            return LiteralValue(value)
        }
    }
}
