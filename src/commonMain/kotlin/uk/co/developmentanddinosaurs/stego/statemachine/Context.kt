package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Holds the extended state of the state machine in an immutable way.
 *
 * The context is a data repository that [Action]s can use to store and retrieve information.
 * In an immutable architecture, the context itself is never modified directly. Instead, an [Action]
 * creates a new context instance with the required changes.
 */
interface Context {
    /**
     * Retrieves a value from the context in a type-safe way.
     *
     * @param T The expected type of the value.
     * @param key The key or path identifying the value to retrieve.
     * @return The value associated with the key, cast to type [T], or null if the key is not found or the type is wrong.
     */
    fun <T> getData(key: String): T?
}
