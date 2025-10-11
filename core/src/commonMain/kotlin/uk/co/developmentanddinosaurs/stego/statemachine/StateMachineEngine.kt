package uk.co.developmentanddinosaurs.stego.statemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * A concrete state machine engine that executes a given [StateMachineDefinition].
 *
 * @param definition The static blueprint of the state machine to execute.
 * @param scope The [CoroutineScope] in which invokable services will be launched. Defaults to a new scope with a SupervisorJob.
 */
class StateMachineEngine(
    private val definition: StateMachineDefinition,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
) {

    private val _currentState: MutableStateFlow<State>
    private val _context: MutableStateFlow<Context>

    val currentState: StateFlow<State> get() = _currentState.asStateFlow()
    val context: StateFlow<Context> get() = _context.asStateFlow()

    init {
        val initialState = definition.states[definition.initial]
            ?: throw IllegalArgumentException("Initial state '${definition.initial}' not found in definition.")
        _currentState = MutableStateFlow(initialState)
        _context = MutableStateFlow(definition.initialContext)

        enterState(initialState, Event("stego.internal.init"))
    }

    fun send(event: Event) {
        try {
            val sourceState = _currentState.value
            val transition = findTransition(event)

            if (transition != null) {
                val targetState = resolveTargetState(transition)
                if (targetState != null) {
                    executeTransition(sourceState, targetState, transition, event)
                }
            }
        } catch (e: Exception) {
            val errorEvent = Event(
                type = "error.execution",
                data = mapOf("cause" to (e.message ?: "An unknown execution error occurred"))
            )
            send(errorEvent)
        }
    }

    private fun findTransition(event: Event): Transition? {
        return _currentState.value.on[event.type]?.firstOrNull { transition ->
            transition.guard?.evaluate(_context.value, event) ?: true
        }
    }

    private fun executeTransition(sourceState: State, targetState: State, transition: Transition, event: Event) {
        var tempContext = _context.value
        tempContext = sourceState.onExit.fold(tempContext) { acc, action -> action.execute(acc, event) }
        tempContext = transition.actions.fold(tempContext) { acc, action -> action.execute(acc, event) }
        _context.value = tempContext

        enterState(targetState, event)
    }

    private fun enterState(state: State, event: Event) {
        var tempContext = _context.value
        tempContext = state.onEntry.fold(tempContext) { acc, action -> action.execute(acc, event) }
        _context.value = tempContext
        _currentState.value = state

        state.invoke?.let { invokable ->
            val deferredEvent = invokable.invoke(_context.value, scope)
            scope.launch {
                val resultEvent = deferredEvent.await()
                send(resultEvent)
            }
        }
    }

    private fun resolveTargetState(transition: Transition): State? {
        return definition.states[transition.target]
    }
}
