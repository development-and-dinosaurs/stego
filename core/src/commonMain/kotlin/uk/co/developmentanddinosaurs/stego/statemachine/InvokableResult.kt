package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents the result of an [Invokable] execution.
 */
sealed interface InvokableResult {
    /**
     * Represents a successful execution of an [Invokable].
     * @property data The data payload produced by the successful execution.
     */
    data class Success(
        val data: Map<String, DataValue> = emptyMap(),
    ) : InvokableResult

    /**
     * Represents a failed execution of an [Invokable].
     * @property context The updated context after the failed execution.
     * @property cause An optional [Throwable] that represents the reason for failure.
     */
    data class Failure(
        val data: Map<String, DataValue> = emptyMap(),
        val cause: Throwable? = null,
    ) : InvokableResult
}
