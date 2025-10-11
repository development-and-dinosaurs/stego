package uk.co.developmentanddinosaurs.stego.statemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

/**
 * Represents a long-running service or another process that can be invoked by a [kotlin.collections.State].
 *
 * This is a regular interface to allow library consumers to provide their own custom implementations
 * for tasks like API calls, database operations, etc.
 */
interface Invokable {
    /**
     * Starts the invokable service within the given coroutine scope.
     *
     * The implementation should launch its work within the provided [scope] (e.g., using `scope.async`) and
     * is contractually obligated to return a [Deferred] that will eventually resolve to a completion or error [Event].
     *
     * @param context The current, immutable context of the state machine.
     * @param scope The [CoroutineScope] in which the asynchronous work should be launched.
     * @return A [Deferred] instance that will complete with the resulting [Event].
     */
    fun invoke(context: Context, scope: CoroutineScope): Deferred<Event>
}
