package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * An immutable, type-safe store for holding the state machine's data.
 *
 * It behaves like a map but provides a more constrained API to ensure immutability.
 * Every "put" operation returns a new Context instance with the data updated.
 */
class Context private constructor(private val values: Map<String, Any>) {

    /**
     * Creates an empty context.
     */
    constructor() : this(emptyMap())

    /**
     * Retrieves a value from the context for a given [key].
     *
     * @param key The key of the value to retrieve.
     * @return The value as type [T] if it exists and is of the correct type, otherwise null.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T {
        return values[key] as T
    }

    /**
     * Returns a new [Context] instance with the given [key] and [value] added.
     *
     * If the key already exists, its value will be overwritten.
     *
     * @param key The key for the data.
     * @param value The data to store.
     * @return A new, updated Context instance.
     */
    fun put(key: String, value: Any): Context {
        return Context(values + (key to value))
    }
}
