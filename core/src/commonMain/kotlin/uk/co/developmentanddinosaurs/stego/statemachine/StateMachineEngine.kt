package uk.co.developmentanddinosaurs.stego.statemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.min

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
        val initialState = findStateById(definition.initial)
            ?: throw IllegalArgumentException("Initial state '${definition.initial}' not found in definition.")
        _output = MutableStateFlow(StateMachineOutput(initialState, definition.initialContext))

        // Enter the initial state, which will handle descending to the leaf and running entry actions.
        enterState(initialState, Event("stego.internal.init"))
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
                data = mapOf("cause" to StringPrimitive(e.message ?: "An unknown execution error occurred"))
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

        val sourcePath = getPathToState(sourceState.id)!!
        val targetPath = getPathToState(targetState.id)!!
        val lcaIndex = sourcePath.zip(targetPath).indexOfFirst { (a, b) -> a.id != b.id }.let { if (it == -1) min(sourcePath.size, targetPath.size) - 1 else it - 1 }

        val statesToExit = sourcePath.subList(lcaIndex + 1, sourcePath.size).reversed()

        var tempContext = output.value.context

        statesToExit.forEach { state ->
            tempContext = state.onExit.fold(tempContext) { acc, action -> action.execute(acc, event) }
        }

        tempContext = transition.actions.fold(tempContext) { acc, action -> action.execute(acc, event) }
        _output.value = _output.value.copy(context = tempContext)

        val statesToEnter = targetPath.subList(lcaIndex + 1, targetPath.size)
        enterState(targetState, event, statesToEnter)
    }

    private fun enterState(targetState: State, event: Event, statesToEnter: List<State> = getPathToState(targetState.id)!!) {
        var tempContext = output.value.context

        statesToEnter.forEach { state ->
            tempContext = state.onEntry.fold(tempContext) { acc, action -> action.execute(acc, event) }
        }

        var finalTargetState = targetState
        val descentPath = mutableListOf<State>()
        while (finalTargetState.initial != null) {
            val nextStateId = finalTargetState.initial!!
            val nextState = finalTargetState.states[nextStateId] ?: throw IllegalStateException("Nested initial state '$nextStateId' not found in state '${finalTargetState.id}'.")
            descentPath.add(nextState)
            finalTargetState = nextState
        }

        descentPath.forEach { state ->
            tempContext = state.onEntry.fold(tempContext) { acc, action -> action.execute(acc, event) }
        }

        _output.value = StateMachineOutput(finalTargetState, tempContext)

        finalTargetState.invoke?.let {
            activeInvokableJob = scope.launch {
                val resultEvent = it.invoke(output.value.context, scope).await()
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
            findStateById(id, state.states)?.let { return it }
        }
        return null
    }

    private fun findParentState(childId: String, states: Map<String, State> = definition.states): State? {
        for (state in states.values) {
            if (state.states.containsKey(childId)) return state
            findParentState(childId, state.states)?.let { return it }
        }
        return null
    }

    private fun getInitialStatePath(): List<State> {
        var current = findStateById(definition.initial) ?: return emptyList()
        val path = mutableListOf(current)
        while (current.initial != null) {
            current = current.states[current.initial!!] ?: return path
            path.add(current)
        }
        return path
    }

    private fun getPathToState(stateId: String): List<State>? {
        fun find(targetId: String, current: State, path: List<State>): List<State>? {
            val newPath = path + current
            if (current.id == targetId) return newPath
            current.states.values.forEach { child -> find(targetId, child, newPath)?.let { return it } }
            return null
        }
        definition.states.values.forEach { state -> find(stateId, state, emptyList())?.let { return it } }
        return null
    }
}
