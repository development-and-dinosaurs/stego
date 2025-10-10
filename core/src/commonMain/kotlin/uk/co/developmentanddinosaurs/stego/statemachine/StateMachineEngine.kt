package uk.co.developmentanddinosaurs.stego.statemachine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A concrete state machine engine that executes a given [StateMachineDefinition].
 *
 * @param definition The static blueprint of the state machine to execute.
 */
class StateMachineEngine(
    private val definition: StateMachineDefinition
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
    }

    /**
     * Processes an event to find and execute a valid transition.
     * This version evaluates guards and resolves nested states, but does not yet handle actions or transition bubbling.
     */
    fun send(event: Event) {
        val transition = _currentState.value.on[event.type]?.firstOrNull { transition ->
            transition.guard?.evaluate(_context.value, event) ?: true
        }

        if (transition != null) {
            val targetState = definition.states[transition.target]
            if (targetState != null) {
                _currentState.value = targetState
            }
        }
    }
}
