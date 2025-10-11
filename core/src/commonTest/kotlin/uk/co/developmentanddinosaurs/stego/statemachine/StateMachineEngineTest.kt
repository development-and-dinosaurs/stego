package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

// A simple test action that assigns a value to the context.
private data class AssignAction(val key: String, val value: Any) : Action {
    override fun execute(context: Context, event: Event): Context {
        return context.put(key, value)
    }
}

// A test action that appends a value to a list in the context.
private data class AppendAction(val key: String, val value: String) : Action {
    override fun execute(context: Context, event: Event): Context {
        val list = context.get<List<String>?>(key) ?: emptyList()
        return context.put(key, list + value)
    }
}

// A test action designed to always throw an exception.
private object CrashingAction : Action {
    override fun execute(context: Context, event: Event): Context {
        throw IllegalStateException("This action is designed to fail.")
    }
}

// A test invokable that simulates a network call.
private data class TestInvokable(val resultEvent: Event, val duration: Long = 100) : Invokable {
    override fun invoke(context: Context, scope: CoroutineScope): Deferred<Event> {
        return scope.async {
            delay(duration)
            resultEvent
        }
    }
}

class StateMachineEngineTest : BehaviorSpec({
    coroutineTestScope = true

    Given("a state machine with an initial and a next state") {
        val initialState = State(id = "Initial", on = mapOf("NEXT" to listOf(Transition(target = "Next"))))
        val nextState = State(id = "Next")
        val definition = StateMachineDefinition(
            initial = "Initial",
            states = mapOf("Initial" to initialState, "Next" to nextState)
        )

        When("the engine is created") {
            val engine = StateMachineEngine(definition)

            Then("the initial state should be correct") {
                engine.output.value.state shouldBe initialState
            }
        }

        When("an event is sent that triggers a transition") {
            val engine = StateMachineEngine(definition)
            engine.send(Event(type = "NEXT"))

            Then("the state machine should transition to the next state") {
                engine.output.value.state shouldBe nextState
            }
        }

        When("an event is sent that does not trigger a transition") {
            val engine = StateMachineEngine(definition)
            engine.send(Event(type = "UNKNOWN"))

            Then("the state machine should remain in the initial state") {
                engine.output.value.state shouldBe initialState
            }
        }
    }

    Given("a state machine with a guarded transition") {
        val trueGuard = EqualsGuard(LiteralValue(true), LiteralValue(true))
        val falseGuard = EqualsGuard(LiteralValue(true), LiteralValue(false))
        val guardedState = State(
            id = "Guarded",
            on = mapOf(
                "EVENT" to listOf(
                    Transition(target = "Next", guard = falseGuard),
                    Transition(target = "Other", guard = trueGuard)
                )
            )
        )
        val nextState = State(id = "Next")
        val otherState = State(id = "Other")
        val definition = StateMachineDefinition(
            initial = "Guarded",
            states = mapOf("Guarded" to guardedState, "Next" to nextState, "Other" to otherState)
        )

        When("an event is sent that matches a passing guard") {
            val engine = StateMachineEngine(definition)
            engine.send(Event(type = "EVENT"))

            Then("the state machine should transition to the correct state") {
                engine.output.value.state shouldBe otherState
            }
        }
    }

    Given("a state machine with a transition that has an action") {
        val assignAction = AssignAction(key = "assigned", value = true)
        val transition = Transition(target = "Next", actions = listOf(assignAction))
        val initialState = State(id = "Initial", on = mapOf("ACTION_EVENT" to listOf(transition)))
        val nextState = State(id = "Next")
        val definition = StateMachineDefinition(
            initial = "Initial",
            states = mapOf("Initial" to initialState, "Next" to nextState)
        )

        When("an event is sent that triggers the transition") {
            val engine = StateMachineEngine(definition)
            engine.send(Event(type = "ACTION_EVENT"))

            Then("the action should be executed and the context updated") {
                engine.output.value.context.get<Boolean>("assigned") shouldBe true
            }
            Then("the state machine should transition to the next state") {
                engine.output.value.state shouldBe nextState
            }
        }
    }

    Given("a state machine with a transition that has multiple actions") {
        val action1 = AssignAction(key = "action1", value = "ran")
        val action2 = AssignAction(key = "action2", value = 123)
        val transition = Transition(target = "Next", actions = listOf(action1, action2))
        val initialState = State(id = "Initial", on = mapOf("MULTI_ACTION_EVENT" to listOf(transition)))
        val nextState = State(id = "Next")
        val definition = StateMachineDefinition(
            initial = "Initial",
            states = mapOf("Initial" to initialState, "Next" to nextState)
        )

        When("an event is sent that triggers the transition") {
            val engine = StateMachineEngine(definition)
            engine.send(Event(type = "MULTI_ACTION_EVENT"))

            Then("all actions should be executed and the context updated") {
                engine.output.value.context.get<String>("action1") shouldBe "ran"
                engine.output.value.context.get<Int>("action2") shouldBe 123
            }
        }
    }

    Given("a state machine with entry and exit actions") {
        val exitAction = AssignAction("exit", true)
        val transitionAction = AssignAction("transition", true)
        val entryAction = AssignAction("entry", true)

        val initialState = State(id = "Initial", onExit = listOf(exitAction), on = mapOf("MOVE" to listOf(Transition(target = "Next", actions = listOf(transitionAction)))))
        val nextState = State(id = "Next", onEntry = listOf(entryAction))
        val definition = StateMachineDefinition(
            initial = "Initial",
            states = mapOf("Initial" to initialState, "Next" to nextState)
        )

        When("a transition occurs") {
            val engine = StateMachineEngine(definition)
            engine.send(Event("MOVE"))

            Then("the exit, transition, and entry actions should all be executed") {
                engine.output.value.context.get<Boolean>("exit") shouldBe true
                engine.output.value.context.get<Boolean>("transition") shouldBe true
                engine.output.value.context.get<Boolean>("entry") shouldBe true
            }
        }
    }

    Given("a transition with an action that throws an exception") {
        val errorState = State(id = "ErrorState")
        val crashingTransition = Transition(target = "Next", actions = listOf(CrashingAction))
        val errorTransition = Transition(target = "ErrorState")
        val initialState = State(
            id = "Initial",
            on = mapOf(
                "CRASH_EVENT" to listOf(crashingTransition),
                "error.execution" to listOf(errorTransition)
            )
        )
        val nextState = State(id = "Next")
        val definition = StateMachineDefinition(
            initial = "Initial",
            states = mapOf("Initial" to initialState, "Next" to nextState, "ErrorState" to errorState)
        )

        When("the event that causes the crash is sent") {
            val engine = StateMachineEngine(definition)
            engine.send(Event("CRASH_EVENT"))

            Then("the state machine should transition to the error state") {
                engine.output.value.state shouldBe errorState
            }
        }
    }

    Given("a state with an invokable service") {
        val doneEvent = Event(type = "INVOKE_DONE")
        val invokable = TestInvokable(resultEvent = doneEvent)
        val successState = State(id = "Success")
        val loadingState = State(id = "Loading", invoke = invokable, on = mapOf("INVOKE_DONE" to listOf(Transition("Success"))))
        val definition = StateMachineDefinition(
            initial = "Loading",
            states = mapOf("Loading" to loadingState, "Success" to successState)
        )

        When("the engine is created") {
            val engine = StateMachineEngine(definition, this)
            testCoroutineScheduler.advanceUntilIdle()

            Then("the invokable should be executed and the resulting event should cause a transition") {
                engine.output.value.state shouldBe successState
            }
        }
    }

    Given("a state with a cancellable invokable service") {
        val doneEvent = Event(type = "INVOKE_DONE")
        val invokable = TestInvokable(resultEvent = doneEvent, duration = 5000) // A long-running task
        val successState = State(id = "Success")
        val failedTestState = State(id = "FailedTestState")
        // This state now has a "poison pill" transition. If the invokable is not cancelled,
        // it will transition here, failing the test.
        val idleState = State(id = "Idle", on = mapOf("INVOKE_DONE" to listOf(Transition("FailedTestState"))))
        val loadingState = State(
            id = "Loading",
            invoke = invokable,
            on = mapOf(
                "INVOKE_DONE" to listOf(Transition("Success")),
                "CANCEL" to listOf(Transition("Idle"))
            )
        )
        val definition = StateMachineDefinition(
            initial = "Loading",
            states = mapOf("Loading" to loadingState, "Success" to successState, "Idle" to idleState, "FailedTestState" to failedTestState)
        )

        When("the engine is created and a cancel event is sent before the invokable completes") {
            val engine = StateMachineEngine(definition, this)
            engine.send(Event("CANCEL"))
            testCoroutineScheduler.advanceUntilIdle()

            Then("the invokable should be cancelled and the state should be Idle") {
                engine.output.value.state shouldBe idleState
            }
        }
    }

    Given("a hierarchical state machine") {
        val childState = State(id = "Child")
        val parentState = State(
            id = "Parent",
            initial = "Child",
            states = mapOf("Child" to childState)
        )
        val definition = StateMachineDefinition(
            initial = "Parent",
            states = mapOf("Parent" to parentState)
        )

        When("the engine is created") {
            val engine = StateMachineEngine(definition)

            Then("it should automatically enter the nested initial state") {
                engine.output.value.state shouldBe childState
            }
        }
    }

    Given("a hierarchical state machine with a transition on the parent") {
        val endState = State(id = "End")
        val childState = State(id = "Child")
        val parentState = State(
            id = "Parent",
            initial = "Child",
            states = mapOf("Child" to childState),
            on = mapOf("GOTO_END" to listOf(Transition("End")))
        )
        val definition = StateMachineDefinition(
            initial = "Parent",
            states = mapOf("Parent" to parentState, "End" to endState)
        )

        When("the engine is in the child state and an event is sent") {
            val engine = StateMachineEngine(definition)
            engine.send(Event("GOTO_END"))

            Then("the engine should find the transition on the parent and move to the correct state") {
                engine.output.value.state shouldBe endState
            }
        }
    }

    Given("a deeply nested hierarchical state machine") {
        val l5 = State("L5", onEntry = listOf(AppendAction("path", "L5")))
        val l4 = State("L4", initial = "L5", states = mapOf("L5" to l5), onEntry = listOf(AppendAction("path", "L4")))
        val l3 = State("L3", initial = "L4", states = mapOf("L4" to l4), onEntry = listOf(AppendAction("path", "L3")))
        val l2 = State("L2", initial = "L3", states = mapOf("L3" to l3), onEntry = listOf(AppendAction("path", "L2")))
        val l1 = State("L1", initial = "L2", states = mapOf("L2" to l2), onEntry = listOf(AppendAction("path", "L1")))
        val definition = StateMachineDefinition(
            initial = "L1",
            states = mapOf("L1" to l1)
        )

        When("the engine is created") {
            val engine = StateMachineEngine(definition)

            Then("the final state should be the deepest child") {
                engine.output.value.state shouldBe l5
            }

            Then("entry actions for all states in the hierarchy should be executed in order") {
                engine.output.value.context.get<List<String>>("path") shouldBe listOf("L1", "L2", "L3", "L4", "L5")
            }
        }
    }
})
