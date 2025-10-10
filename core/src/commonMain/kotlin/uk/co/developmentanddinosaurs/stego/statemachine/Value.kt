package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a value that can be sourced from different locations within the state machine.
 *
 * This sealed interface is a key part of the declarative guard system. It allows a JSON configuration
 * to explicitly state whether a value for a comparison should come from a hard-coded literal,
 * the machine's context, or the triggering event.
 *
 * @param T The type of the value being represented.
 */
sealed interface Value<T>

/**
 * Represents a value that is retrieved from the state machine's context.
 *
 * @param T The expected type of the value.
 * @property path The key or path used to look up the value in the context.
 */
data class ContextValue<T>(
    val path: String
) : Value<T>

/**
 * Represents a value that is retrieved from the data payload of the triggering [Event].
 *
 * @param T The expected type of the value.
 * @property path The key or path used to look up the value in the [Event]'s data map.
 */
data class EventValue<T>(
    val path: String
) : Value<T>

/**
 * Represents a fixed, literal (or constant) value.
 *
 * @param T The type of the value.
 * @property value The actual hard-coded value.
 */
data class LiteralValue<T>(
    val value: T
) : Value<T>
