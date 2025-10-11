package uk.co.developmentanddinosaurs.stego.statemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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

    private val _output: MutableStateFlow<StateMachineOutput>
    val output: StateFlow<StateMachineOutput> get() = _output.asStateFlow()

    private var activeInvokableJob: Job? = null

    init {
        val initialPath = getInitialStatePath()
        val leafInitialState = initialPath.lastOrNull()
            ?: throw IllegalArgumentException("Initial state '${definition.initial}' not found in definition.")

        var tempContext = definition.initialContext
        initialPath.forEach { state ->
            tempContext = state.onEntry.fold(tempContext) { acc, action -> action.execute(acc, Event("stego.internal.init")) }
        }

        _output = MutableStateFlow(StateMachineOutput(leafInitialState, tempContext))

        // After setting the initial state, check if the final leaf state has an invokable to run.
        leafInitialState.invoke?.let { invokable ->
            val deferredEvent = invokable.invoke(output.value.context, scope)
            activeInvokableJob = scope.launch {
                val resultEvent = deferredEvent.await()
                send(resultEvent)
            }
        }
    }

    fun send(event: Event) {
        try {
            val sourceState = output.value.state
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
        var currentState: State? = output.value.state
        while (currentState != null) {
            val transition = currentState.on[event.type]?.firstOrNull { transition ->
                transition.guard?.evaluate(output.value.context, event) ?: true
            }
            if (transition != null) return transition
            currentState = findParentState(currentState.id)
        }
        return null
    }

    private fun executeTransition(sourceState: State, targetState: State, transition: Transition, event: Event) {
        activeInvokableJob?.cancel()
        activeInvokableJob = null

        var tempContext = output.value.context
        tempContext = sourceState.onExit.fold(tempContext) { acc, action -> action.execute(acc, event) }
        tempContext = transition.actions.fold(tempContext) { acc, action -> action.execute(acc, event) }
        _output.value = _output.value.copy(context = tempContext)

        enterState(targetState, event)
    }

    private fun enterState(state: State, event: Event) {
        var tempContext = output.value.context
        tempContext = state.onEntry.fold(tempContext) { acc, action -> action.execute(acc, event) }
        _output.value = StateMachineOutput(state, tempContext)

        state.invoke?.let { invokable ->
            val deferredEvent = invokable.invoke(output.value.context, scope)
            activeInvokableJob = scope.launch {
                val resultEvent = deferredEvent.await()
                send(resultEvent)
            }
        }
    }

    private fun resolveTargetState(transition: Transition): State? {
        return findStateById(transition.target)
    }

    private fun findStateById(id: String, states: Map<String, State> = definition.states): State? {
        states[id]?.let { return it }
        for (state in states.values) {
            state.states?.let { subStates ->
                findStateById(id, subStates)?.let { return it }
            }
        }
        return null
    }

    private fun findParentState(childId: String, states: Map<String, State> = definition.states): State? {
        for (state in states.values) {
            if (state.states?.containsKey(childId) == true) return state
            state.states?.let { subStates ->
                findParentState(childId, subStates)?.let { return it }
            }
        }
        return null
    }

    private fun getInitialStatePath(): List<State> {
        var current = findStateById(definition.initial) ?: return emptyList()
        val path = mutableListOf(current)
        while (current.initial != null) {
            current = current.states?.get(current.initial!!) ?: return path
            path.add(current)
        }
        return path
    }
}
