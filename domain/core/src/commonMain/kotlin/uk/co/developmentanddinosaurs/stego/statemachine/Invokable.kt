package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a self-contained, long-running, asynchronous unit of work that can be invoked by a
 * [State].
 *
 * An `Invokable` is a piece of logic that receives all the data it needs to perform its task via an
 * `input` map.
 *
 * This is a functional interface or Single Abstract Method (SAM) interface (`fun interface`),
 * allowing for concise implementation via lambdas using SAM conversion. For example, you could
 * define an Invokable like this:
 * ```kotlin
 * val myInvokable: Invokable = object : Invokable {
 *     override suspend fun invoke(context: Context): InvokableResult {
 *         return InvokableResult.Success(mapOf())
 *     }
 * }
 * ```
 *
 * But with SAM conversion, you can get the same result with a simple lambda:
 * ```kotlin
 * val invokable = Invokable { _ -> InvokableResult.Success(mapOf()) }
 * ```
 *
 * # Execution Flow
 * 1. When the state machine enters a state containing an [InvokableDefinition], the
 *    `StateMachineEngine` resolves the `input` parameters from the definition (evaluating any
 *    context expressions).
 * 2. The engine then launches the `invoke(input)` function in a background coroutine, passing the
 *    resolved input.
 * 3. The state machine **does not wait** for the invokable to complete and remains responsive to
 *    other events.
 * 4. The `invoke` function performs its work and must return an [InvokableResult]
 *    ([InvokableResult.Success] or [InvokableResult.Failure]).
 * 5. The engine then raises a new event back to itself:
 *     - `done.invoke.<id>` for a [InvokableResult.Success].
 *     - `error.invoke.<id>` for a [InvokableResult.Failure].
 *
 *   Both results contain any extra result data in the result payload, whereas the failure result
 *   also includes an optional [Throwable] cause
 *
 * # Parameter and Context Handling
 * The `input` map passed to the `invoke` method contains data that conforms to the state machine's
 * internal data model ([DataValue]). This ensures type consistency and predictability. To access a
 * primitive value, you will need to cast to the specific `DataValue` type (e.g., `StringPrimitive`)
 * and then access its `value`.
 *
 * An `Invokable` **cannot** directly modify the state machine's context. Instead, it returns data
 * in its [InvokableResult]. This data is placed into the resulting `done` or `error` event, and it
 * is the responsibility of [Action]s in the subsequent transition (e.g., `assign`) to update the
 * context with this data. *
 *
 * @sample
 *
 * ```kotlin
 * // An example invokable that fetches a user from an API, configured via parameters.
 * val fetchUserInvokable = Invokable { input ->
 *     val userId = (params["input"] as? StringPrimitive)?.value
 *     if (userId != null) {
 *         try {
 *             val user = apiClient.fetchUser(userId)
 *             InvokableResult.Success(data = mapOf("user" to user.toDataValue()))
 *         } catch (e: Exception) {
 *             InvokableResult.Failure(data = mapOf("error" to "server error"), cause = e)
 *         }
 *     } else {
 *         InvokableResult.Failure(data = mapOf("error" to "missing user id"))
 *     }
 * }
 * ```
 */
fun interface Invokable {
  /**
   * Executes the self-contained, long-running work.
   *
   * @param input A map of resolved parameters for this specific invocation.
   * @return An [InvokableResult] representing the outcome of the work.
   */
  suspend fun invoke(input: Map<String, Any?>): InvokableResult
}
