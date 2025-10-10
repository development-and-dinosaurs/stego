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

    val currentState: StateFlow<State>

    init {
        val initialState = definition.states[definition.initial]
            ?: throw IllegalArgumentException("Initial state '${definition.initial}' not found in definition.")
        _currentState = MutableStateFlow(initialState)
        currentState = _currentState.asStateFlow()
    }

    /**
     * Processes an event to find the target state and transition to it.
     * State changes are emitted to the [currentState] flow.
     */
    fun send(event: Event) {
        val transition = _currentState.value.on[event.type]?.firstOrNull()

        if (transition != null) {
            val targetState = definition.states[transition.target]
            if (targetState != null) {
                _currentState.value = targetState
            }
        }
    }
}
