package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a value that can be sourced from different locations within the state machine.
 *
 * This sealed interface is a key part of the declarative guard system. It allows a JSON configuration
 * to explicitly state whether a value for a comparison should come from a hard-coded literal,
 * the machine's [Context], or the triggering [Event].
 *
 * @param T The type of the value being represented.
 */
sealed interface Value<T>

/**
 * Represents a value that is retrieved from the state machine's [Context].
 *
 * @param T The expected type of the value.
 */
interface ContextValue<T> : Value<T> {
    /**
     * The key or path used to look up the value in the [Context].
     * For nested data, this might be a dot-separated path like "user.profile.age".
     */
    val path: String
}

/**
 * Represents a value that is retrieved from the data payload of the triggering [Event].
 *
 * @param T The expected type of the value.
 */
interface EventValue<T> : Value<T> {
    /**
     * The key or path used to look up the value in the [Event]'s data map.
     * For nested data, this might be a dot-separated path like "payload.order.total".
     */
    val path: String
}

/**
 * Represents a fixed, literal (or constant) value.
 *
 * @param T The type of the value.
 */
interface LiteralValue<T> : Value<T> {
    /**
     * The actual hard-coded value.
     */
    val value: T
}
