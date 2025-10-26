package uk.co.developmentanddinosaurs.stego.statemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import uk.co.developmentanddinosaurs.stego.statemachine.valueresolution.ValueProvider
import kotlin.math.min

/**
 * A concrete state machine engine that executes a given [StateMachineDefinition].
 * This engine guarantees run-to-completion semantics by processing events sequentially.
 *
 * @param definition The static blueprint of the state machine to execute.
 * @param scope The [CoroutineScope] in which invokable services and the event processor will be launched.
 * Defaults to a new scope with a SupervisorJob.
 */
class StateMachineEngine(
    private val definition: StateMachineDefinition,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob()),
) {
    private val _output: MutableStateFlow<StateMachineOutput>
    val output: StateFlow<StateMachineOutput> get() = _output.asStateFlow()
    private val processingMutex = Mutex()

    private var activeInvokableJob: Job? = null

    private val stateMap: Map<String, State>
    private val parentMap: Map<String, State>

    init {
        stateMap = buildStateMap(definition.states)
        parentMap = buildParentMap(definition.states)

        validateDefinition()

        val initialState = stateMap[definition.initial]!! // We've just validated this is here
        _output = MutableStateFlow(StateMachineOutput(initialState, Context(definition.initialContext)))

        enterState(initialState, Event("stego.internal.init"))
    }

    /**
     * Sends an event to the state machine for processing. This method is non-blocking and thread-safe.
     */
    fun send(event: Event) {
        println("current context")
        println(output.value.context.asMap())
        println("current event")
        println(event.data)
        scope.launch {
            processingMutex.withLock {
                processEvent(event)
            }
        }
    }

    /**
     * The internal, sequential event processor that guarantees run-to-completion.
     */
    private fun processEvent(event: Event) {
        try {
            val currentState = output.value.state
            val (transitionSourceState, transition) = findTransition(event) ?: return
            if (transition.target == null) {
                val tempContext = output.value.context.let { context ->
                    transition.actions.fold(context) { acc, action -> action.execute(acc, event) }
                }
                _output.value = _output.value.copy(context = tempContext)
                return
            }

            val targetState = stateMap.getValue(transition.target)
            executeTransition(currentState, transitionSourceState, targetState, transition, event)
        } catch (e: Exception) {
            if (event.type == "error.execution") {
                return
            }
            val errorEvent =
                Event(
                    type = "error.execution",
                    data = mapOf("cause" to (e.message ?: "An unknown execution error occurred")),
                )
            send(errorEvent)
        }
    }

    private fun validateDefinition() {
        if (!stateMap.containsKey(definition.initial)) {
            throw StateMachineException("Initial state '${definition.initial}' not found in definition.")
        }
        for (state in stateMap.values) {
            state.on.values.flatten().forEach { transition ->
                transition.target?.let { target ->
                    if (!stateMap.containsKey(target)) {
                        throw StateMachineException("State '${state.id}' has a transition to non-existent target state '$target'.")
                    }
                }
            }
            state.initial?.let { initialId ->
                if (!state.states.containsKey(initialId)) {
                    throw StateMachineException("State '${state.id}' has a non-existent initial state '$initialId'.")
                }
            }
        }
    }

    private fun findTransition(event: Event): Pair<State, Transition>? {
        var currentState: State? = output.value.state
        while (currentState != null) {
            val transition =
                currentState.on[event.type]?.firstOrNull { transition ->
                    transition.guard?.evaluate(output.value.context, event) ?: true
                }
            if (transition != null) return currentState to transition
            currentState = findParentState(currentState.id)
        }
        return null
    }

    private fun executeTransition(
        currentState: State,
        transitionSourceState: State,
        targetState: State,
        transition: Transition,
        event: Event,
    ) {
        activeInvokableJob?.cancel()
        activeInvokableJob = null

        // The state machine's validation guarantees these paths will exist.
        val sourcePath = getPathToState(currentState.id)
        val targetPath = getPathToState(targetState.id)

        val lcaIndex =
            (0 until min(sourcePath.size, targetPath.size))
                .firstOrNull { i -> sourcePath[i].id != targetPath[i].id }
                ?.let { it - 1 } ?: (min(sourcePath.size, targetPath.size) - 1)

        // If the source and target are the same, we need to exit and re-enter the state.
        // Decrementing the LCA index achieves this by including the state in the exit/entry paths.
        val finalLcaIndex =
            if (transitionSourceState.id == targetState.id) lcaIndex - 1 else lcaIndex

        val statesToExit = sourcePath.subList(finalLcaIndex + 1, sourcePath.size).reversed()

        var tempContext = output.value.context

        statesToExit.forEach { state ->
            tempContext = state.onExit.fold(tempContext) { acc, action -> action.execute(acc, event) }
        }

        tempContext = transition.actions.fold(tempContext) { acc, action -> action.execute(acc, event) }
        _output.value = _output.value.copy(context = tempContext)

        val statesToEnter = targetPath.subList(finalLcaIndex + 1, targetPath.size)
        enterState(targetState, event, statesToEnter)
    }

    private fun enterState(
        targetState: State,
        event: Event,
        statesToEnter: List<State>? = null,
    ) {
        val path = statesToEnter ?: getPathToState(targetState.id)

        var tempContext = output.value.context

        path.forEach { state ->
            tempContext = state.onEntry.fold(tempContext) { acc, action -> action.execute(acc, event) }
        }

        var finalTargetState = targetState
        val descentPath = mutableListOf<State>()
        // Only descend into initial substates if we are actually entering the target state,
        // not just transitioning to it as an ancestor.
        if (path.isNotEmpty()) {
            while (finalTargetState.initial != null) {
                val nextStateId = finalTargetState.initial
                // The validation in the constructor guarantees that this nested initial state will always exist.
                val nextState = finalTargetState.states[nextStateId]!!
                descentPath.add(nextState)
                finalTargetState = nextState
            }
        }

        descentPath.forEach { state ->
            tempContext = state.onEntry.fold(tempContext) { acc, action -> action.execute(acc, event) }
        }

        _output.value = StateMachineOutput(finalTargetState, tempContext)

        finalTargetState.invoke?.let {
            activeInvokableJob =
                scope.launch {
                    val resolvedParams =
                        it.input.map { input ->
                            input.key to ValueProvider.resolve(input.value).get(_output.value.context, null)
                        }.toMap()
                    try {
                        when (
                            val result = it.src.invoke(resolvedParams)
                        ) {
                            is InvokableResult.Success -> {
                                val doneEvent = Event("done.invoke.${it.id}", result.data)
                                send(doneEvent)
                            }

                            is InvokableResult.Failure -> {
                                val errorEvent = Event("error.invoke.${it.id}", result.data)
                                send(errorEvent)
                            }
                        }
                    } catch (e: Exception) {
                        val errorData =
                            mapOf("cause" to (e.message ?: "An unexpected error occurred during invoke"))
                        val errorEvent = Event("error.invoke.${it.id}", errorData)
                        send(errorEvent)
                    }
                }
        }
    }

    private fun findParentState(childId: String): State? = parentMap[childId]

    private fun getPathToState(stateId: String): List<State> {
        val path = mutableListOf<State>()
        var current = stateMap.getValue(stateId)
        while (true) {
            path.add(current)
            current = parentMap[current.id] ?: break
        }
        return path.reversed()
    }

    private fun buildStateMap(states: Map<String, State>): Map<String, State> {
        val map = mutableMapOf<String, State>()

        fun recurse(subStates: Map<String, State>) {
            for (state in subStates.values) {
                map[state.id] = state
                recurse(state.states)
            }
        }
        recurse(states)
        return map
    }

    private fun buildParentMap(states: Map<String, State>): Map<String, State> {
        val map = mutableMapOf<String, State>()

        fun recurse(parent: State?) {
            val children = parent?.states ?: states
            for (child in children.values) {
                if (parent != null) {
                    map[child.id] = parent
                }
                recurse(child)
            }
        }
        recurse(null)
        return map
    }
}
