package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import uk.co.developmentanddinosaurs.stego.statemachine.guards.Guard

// A simple test action that assigns a primitive value to the context.
private data class AssignAction(
    val key: String,
    val value: Any,
) : Action {
    override fun execute(
        context: Context,
        event: Event,
    ): Context = context.put(key, value)
}

// A test action that appends a string to a "trace" list in the context.
private data class TraceAction(
    val name: String,
) : Action {
    override fun execute(
        context: Context,
        event: Event,
    ): Context {
        val currentTrace = context.get("trace") as List<String>
        return context.put("trace", currentTrace + name)
    }
}

// A test action designed to always throw an exception.
private object CrashingAction : Action {
    override fun execute(
        context: Context,
        event: Event,
    ): Context = throw IllegalStateException("This action is designed to fail.")
}

// A test action that sends an event back to the state machine engine.
private class SendAction(
    val event: Event,
) : Action {
    lateinit var engine: StateMachineEngine

    override fun execute(
        context: Context,
        event: Event,
    ): Context {
        engine.send(this.event)
        return context
    }
}

/**
 * An action that introduces an artificial delay.
 * NOTE: This uses Thread.sleep(), which is generally discouraged with coroutines.
 * However, it's the most straightforward way to test event queuing without
 * a major refactoring to make the Action interface suspendable.
 */
private data class DelayAction(
    val duration: Long,
) : Action {
    override fun execute(
        context: Context,
        event: Event,
    ): Context {
        runBlocking { delay(duration) }
        return context
    }
}

class TestInvokable(
    private val duration: Long = 1000,
) : Invokable {
    override suspend fun invoke(input: Map<String, Any?>): InvokableResult {
        delay(duration)
        return InvokableResult.Success(mapOf("waitedFor" to duration))
    }
}

class StateMachineEngineTest :
    BehaviorSpec({
        coroutineTestScope = true

        Given("a state machine with an initial state not in the state map") {
            val definition =
                StateMachineDefinition(
                    initial = "StateA",
                    states = mapOf("StateB" to LogicState(id = "StateB")),
                )
            When("the engine is created") {
                val engine = shouldThrow<StateMachineException> { StateMachineEngine(definition) }

                Then("a state machine exception should be throw") {
                    engine.message shouldBe "Initial state 'StateA' not found in definition."
                }
            }
        }

        Given("a state machine with a transition to a non-existent state") {
            val stateA = LogicState(id = "StateA", on = mapOf("EVENT" to listOf(Transition(target = "StateC"))))
            val definition =
                StateMachineDefinition(
                    initial = "StateA",
                    states = mapOf("StateA" to stateA),
                )

            When("the engine is created") {
                val exception = shouldThrow<StateMachineException> { StateMachineEngine(definition) }

                Then("a state machine exception should be thrown") {
                    exception.message shouldBe "State 'StateA' has a transition to non-existent target state 'StateC'."
                }
            }
        }

        Given("a state machine with a nested non-existent initial state") {
            val parentState =
                LogicState(id = "Parent", initial = "Child", states = mapOf("NotChild" to LogicState(id = "NotChild")))
            val definition =
                StateMachineDefinition(
                    initial = "Parent",
                    states = mapOf("Parent" to parentState),
                )

            When("the engine is created") {
                val exception = shouldThrow<StateMachineException> { StateMachineEngine(definition) }

                Then("a state machine exception should be thrown") {
                    exception.message shouldBe "State 'Parent' has a non-existent initial state 'Child'."
                }
            }
        }

        Given("a state machine with an initial and a next state") {
            val initialState = LogicState(id = "Initial", on = mapOf("NEXT" to listOf(Transition(target = "Next"))))
            val nextState = LogicState(id = "Next")
            val definition =
                StateMachineDefinition(
                    initial = "Initial",
                    states = mapOf("Initial" to initialState, "Next" to nextState),
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
                    engine.output.first { it.state == nextState }
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
            val trueGuard = Guard.create("(true == true)")
            val falseGuard = Guard.create("(true == false)")
            val guardedState =
                LogicState(
                    id = "Guarded",
                    on =
                        mapOf(
                            "EVENT" to
                                    listOf(
                                        Transition(target = "Next", guard = falseGuard),
                                        Transition(target = "Other", guard = trueGuard),
                                    ),
                        ),
                )
            val nextState = LogicState(id = "Next")
            val otherState = LogicState(id = "Other")
            val definition =
                StateMachineDefinition(
                    initial = "Guarded",
                    states = mapOf("Guarded" to guardedState, "Next" to nextState, "Other" to otherState),
                )

            When("an event is sent that matches a passing guard") {
                val engine = StateMachineEngine(definition)
                engine.send(Event(type = "EVENT"))

                Then("the state machine should transition to the correct state") {
                    engine.output.first { it.state == otherState }
                    engine.output.value.state shouldBe otherState
                }
            }
        }

        Given("a state machine with a transition that has an action") {
            val assignAction = AssignAction(key = "assigned", value = true)
            val transition = Transition(target = "Next", actions = listOf(assignAction))
            val initialState = LogicState(id = "Initial", on = mapOf("ACTION_EVENT" to listOf(transition)))
            val nextState = LogicState(id = "Next")
            val definition =
                StateMachineDefinition(
                    initial = "Initial",
                    states = mapOf("Initial" to initialState, "Next" to nextState),
                )

            When("an event is sent that triggers the transition") {
                val engine = StateMachineEngine(definition)
                engine.send(Event(type = "ACTION_EVENT"))

                Then("the action should be executed and the context updated") {
                    val finalOutput = engine.output.first { it.state == nextState }
                    finalOutput.context.get("assigned") shouldBe true
                    finalOutput.state shouldBe nextState
                }
            }
        }

        Given("a state machine with a transition that has multiple actions") {
            val action1 = AssignAction(key = "action1", value = "ran")
            val action2 = AssignAction(key = "action2", value = 123)
            val transition = Transition(target = "Next", actions = listOf(action1, action2))
            val initialState = LogicState(id = "Initial", on = mapOf("MULTI_ACTION_EVENT" to listOf(transition)))
            val nextState = LogicState(id = "Next")
            val definition =
                StateMachineDefinition(
                    initial = "Initial",
                    states = mapOf("Initial" to initialState, "Next" to nextState),
                )

            When("an event is sent that triggers the transition") {
                val engine = StateMachineEngine(definition)
                engine.send(Event(type = "MULTI_ACTION_EVENT"))

                Then("all actions should be executed and the context updated") {
                    val finalOutput = engine.output.first { it.state == nextState }
                    finalOutput.context.get("action1") shouldBe "ran"
                    finalOutput.context.get("action2") shouldBe 123L
                }
            }
        }

        Given("a state machine with entry and exit actions") {
            val exitAction = AssignAction("exit", true)
            val transitionAction = AssignAction("transition", true)
            val entryAction = AssignAction("entry", true)

            val initialState =
                LogicState(
                    id = "Initial",
                    onExit = listOf(exitAction),
                    on = mapOf("MOVE" to listOf(Transition(target = "Next", actions = listOf(transitionAction)))),
                )
            val nextState = LogicState(id = "Next", onEntry = listOf(entryAction))
            val definition =
                StateMachineDefinition(
                    initial = "Initial",
                    states = mapOf("Initial" to initialState, "Next" to nextState),
                )

            When("a transition occurs") {
                val engine = StateMachineEngine(definition)
                engine.send(Event("MOVE"))

                Then("the exit, transition, and entry actions should all be executed") {
                    val finalOutput = engine.output.first { it.state == nextState }
                    finalOutput.context.get("exit") shouldBe true
                    finalOutput.context.get("transition") shouldBe true
                    finalOutput.context.get("entry") shouldBe true
                }
            }
        }

        Given("a transition with an action that throws an exception") {
            val errorState = LogicState(id = "ErrorState")
            val crashingTransition = Transition(target = "Next", actions = listOf(CrashingAction))
            val errorTransition = Transition(target = "ErrorState")
            val initialState =
                LogicState(
                    id = "Initial",
                    on =
                        mapOf(
                            "CRASH_EVENT" to listOf(crashingTransition),
                            "error.execution" to listOf(errorTransition),
                        ),
                )
            val nextState = LogicState(id = "Next")
            val definition =
                StateMachineDefinition(
                    initial = "Initial",
                    states = mapOf("Initial" to initialState, "Next" to nextState, "ErrorState" to errorState),
                )

            When("the event that causes the crash is sent") {
                val engine = StateMachineEngine(definition)
                engine.send(Event("CRASH_EVENT"))

                Then("the state machine should transition to the error state") {
                    engine.output.first { it.state == errorState }
                    engine.output.value.state shouldBe errorState
                }
            }
        }

        Given("a state with an invokable service") {
            val invokable = TestInvokable()
            val successState = LogicState(id = "Success")
            val loadingState =
                LogicState(
                    id = "Loading",
                    invoke = InvokableDefinition(id = "testInvoke", src = invokable),
                    on = mapOf("done.invoke.testInvoke" to listOf(Transition("Success"))),
                )
            val definition =
                StateMachineDefinition(
                    initial = "Loading",
                    states = mapOf("Loading" to loadingState, "Success" to successState),
                )

            When("the engine is created") {
                val engine = StateMachineEngine(definition, this)

                Then("the invokable should be executed and the resulting event should cause a transition") {
                    engine.output.first { it.state == successState }
                }
            }
        }

        Given("a state with a cancellable invokable service") {
            val invokable = TestInvokable(duration = 5000) // A long-running task
            val successState = LogicState(id = "Success")
            val failedTestState = LogicState(id = "FailedTestState")
            val idleState =
                LogicState(id = "Idle", on = mapOf("done.invoke.testInvoke" to listOf(Transition("FailedTestState"))))
            val loadingState =
                LogicState(
                    id = "Loading",
                    invoke = InvokableDefinition(id = "testInvoke", src = invokable),
                    on =
                        mapOf(
                            "done.invoke.testInvoke" to listOf(Transition("Success")),
                            "CANCEL" to listOf(Transition("Idle")),
                        ),
                )
            val definition =
                StateMachineDefinition(
                    initial = "Loading",
                    states =
                        mapOf(
                            "Loading" to loadingState,
                            "Success" to successState,
                            "Idle" to idleState,
                            "FailedTestState" to failedTestState,
                        ),
                )

            When("the engine is created and a cancel event is sent before the invokable completes") {
                val engine = StateMachineEngine(definition, this)
                engine.send(Event("CANCEL"))

                Then("the invokable should be cancelled and the state should be Idle") {
                    engine.output.first { it.state == idleState }
                    engine.output.value.state shouldBe idleState
                }
            }
        }

        Given("a hierarchical state machine") {
            val childState = LogicState(id = "Child")
            val parentState =
                LogicState(
                    id = "Parent",
                    initial = "Child",
                    states = mapOf("Child" to childState),
                )
            val definition =
                StateMachineDefinition(
                    initial = "Parent",
                    states = mapOf("Parent" to parentState),
                )

            When("the engine is created") {
                val engine = StateMachineEngine(definition)

                Then("it should automatically enter the nested initial state") {
                    engine.output.value.state shouldBe childState
                }
            }
        }

        Given("a hierarchical state machine with a transition on the parent") {
            val endState = LogicState(id = "End")
            val childState = LogicState(id = "Child")
            val parentState =
                LogicState(
                    id = "Parent",
                    initial = "Child",
                    states = mapOf("Child" to childState),
                    on = mapOf("GOTO_END" to listOf(Transition("End"))),
                )
            val definition =
                StateMachineDefinition(
                    initial = "Parent",
                    states = mapOf("Parent" to parentState, "End" to endState),
                )

            When("the engine is in the child state and an event is sent") {
                val engine = StateMachineEngine(definition)
                engine.send(Event("GOTO_END"))

                Then("the engine should find the transition on the parent and move to the correct state") {
                    engine.output.first { it.state == endState }
                    engine.output.value.state shouldBe endState
                }
            }
        }

        Given("a hierarchical state machine with entry actions") {
            val childEntryAction = TraceAction("child_entry")
            val parentEntryAction = TraceAction("parent_entry")
            val childState = LogicState(id = "Child", onEntry = listOf(childEntryAction))
            val parentState =
                LogicState(
                    id = "Parent",
                    initial = "Child",
                    states = mapOf("Child" to childState),
                    onEntry = listOf(parentEntryAction),
                )
            val definition =
                StateMachineDefinition(
                    initial = "Parent",
                    initialContext = Context().put("trace", listOf<String>()),
                    states = mapOf("Parent" to parentState),
                )

            When("the engine is created") {
                val engine = StateMachineEngine(definition)

                Then("it should execute entry actions for both parent and child in order") {
                    engine.output.value.context.get("trace") shouldBe listOf("parent_entry", "child_entry")
                }
            }
        }

        Given("a hierarchical transition between sibling states") {
            val parentEntryAction = TraceAction("parent_entry")
            val parentExitAction = TraceAction("parent_exit")
            val childOneEntryAction = TraceAction("childOne_entry")
            val childOneExitAction = TraceAction("childOne_exit")
            val childTwoEntryAction = TraceAction("childTwo_entry")

            val childTwo = LogicState(id = "ChildTwo", onEntry = listOf(childTwoEntryAction))
            val childOne =
                LogicState(
                    id = "ChildOne",
                    onEntry = listOf(childOneEntryAction),
                    onExit = listOf(childOneExitAction),
                    on = mapOf("MOVE" to listOf(Transition("ChildTwo"))),
                )
            val parent =
                LogicState(
                    id = "Parent",
                    initial = "ChildOne",
                    onEntry = listOf(parentEntryAction),
                    onExit = listOf(parentExitAction),
                    states = mapOf("ChildOne" to childOne, "ChildTwo" to childTwo),
                )
            val definition =
                StateMachineDefinition(
                    initial = "Parent",
                    initialContext = Context().put("trace", listOf<String>()),
                    states = mapOf("Parent" to parent),
                )

            When("a transition occurs between the siblings") {
                val engine = StateMachineEngine(definition)
                engine.send(Event("MOVE"))

                Then("only the relevant entry and exit actions should be executed") {
                    engine.output.first { it.state == childTwo }.context.get("trace") shouldBe listOf(
                        "parent_entry",
                        "childOne_entry",
                        "childOne_exit",
                        "childTwo_entry",
                    )
                }
            }
        }

        Given("a deeply nested hierarchical state machine") {
            val l5 = LogicState(id = "l5", onEntry = listOf(TraceAction("l5_entry")))
            val l4 = LogicState(
                id = "l4",
                initial = "l5",
                states = mapOf("l5" to l5),
                onEntry = listOf(TraceAction("l4_entry"))
            )
            val l3 = LogicState(
                id = "l3",
                initial = "l4",
                states = mapOf("l4" to l4),
                onEntry = listOf(TraceAction("l3_entry"))
            )
            val l2 = LogicState(
                id = "l2",
                initial = "l3",
                states = mapOf("l3" to l3),
                onEntry = listOf(TraceAction("l2_entry"))
            )
            val l1 = LogicState(
                id = "l1",
                initial = "l2",
                states = mapOf("l2" to l2),
                onEntry = listOf(TraceAction("l1_entry"))
            )
            val definition =
                StateMachineDefinition(
                    initial = "l1",
                    initialContext = Context().put("trace", listOf<String>()),
                    states = mapOf("l1" to l1),
                )

            When("the engine is created") {
                val engine = StateMachineEngine(definition)

                Then("it should enter the deepest initial state") {
                    engine.output.value.state shouldBe l5
                }
                Then("it should execute all entry actions in order") {

                    engine.output.value.context.get("trace") shouldBe listOf(
                        "l1_entry",
                        "l2_entry",
                        "l3_entry",
                        "l4_entry",
                        "l5_entry",
                    )
                }
            }
        }

        Given("a state machine that sends an event from a transition action") {
            val sendAction = SendAction(Event("EVENT_B"))
            val stateC = LogicState(id = "StateC")
            val stateB =
                LogicState(
                    id = "StateB",
                    on =
                        mapOf(
                            "EVENT_B" to listOf(Transition(target = "StateC")),
                        ),
                )
            val stateA =
                LogicState(
                    id = "StateA",
                    on =
                        mapOf(
                            "EVENT_A" to listOf(Transition(target = "StateB", actions = listOf(sendAction))),
                        ),
                )
            val definition =
                StateMachineDefinition(
                    initial = "StateA",
                    states = mapOf("StateA" to stateA, "StateB" to stateB, "StateC" to stateC),
                )

            When("an event triggers the action that sends another event") {
                val engine = StateMachineEngine(definition, this)
                sendAction.engine = engine
                engine.send(Event("EVENT_A"))

                Then("the machine should run to completion and end in the final state") {
                    engine.output.first { it.state == stateC }
                    engine.output.value.state shouldBe stateC
                }
            }
        }

        Given("a state machine that queues events") {
            val stateC = LogicState(id = "StateC")
            val stateB =
                LogicState(
                    id = "StateB",
                    on = mapOf("EVENT_B" to listOf(Transition("StateC"))),
                )
            val stateA =
                LogicState(
                    id = "StateA",
                    on =
                        mapOf(
                            "EVENT_A" to
                                    listOf(
                                        Transition(
                                            target = "StateB",
                                            actions = listOf(DelayAction(10)),
                                        ),
                                    ),
                        ),
                )
            val definition =
                StateMachineDefinition(
                    initial = "StateA",
                    states = mapOf("StateA" to stateA, "StateB" to stateB, "StateC" to stateC),
                )

            When("two events are sent in quick succession") {
                val engine = StateMachineEngine(definition, this)
                engine.send(Event("EVENT_A"))
                engine.send(Event("EVENT_B"))

                Then("the machine should process both events sequentially and end in the final state") {
                    engine.output.first { it.state == stateC }
                    engine.output.value.state shouldBe stateC
                }
            }
        }
    })
