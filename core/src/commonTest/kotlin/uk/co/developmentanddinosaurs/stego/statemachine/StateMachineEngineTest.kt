package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

// A simple test action that assigns a value to the context.
private data class AssignAction(val key: String, val value: Any) : Action {
    override fun execute(context: Context, event: Event): Context {
        return context.put(key, value)
    }
}

class StateMachineEngineTest : BehaviorSpec({
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
                engine.currentState.value shouldBe initialState
            }
        }

        When("an event is sent that triggers a transition") {
            val engine = StateMachineEngine(definition)
            engine.send(Event(type = "NEXT"))

            Then("the state machine should transition to the next state") {
                engine.currentState.value shouldBe nextState
            }
        }

        When("an event is sent that does not trigger a transition") {
            val engine = StateMachineEngine(definition)
            engine.send(Event(type = "UNKNOWN"))

            Then("the state machine should remain in the initial state") {
                engine.currentState.value shouldBe initialState
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
                engine.currentState.value shouldBe otherState
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
                engine.context.value.get<Boolean>("assigned") shouldBe true
            }
            Then("the state machine should transition to the next state") {
                engine.currentState.value shouldBe nextState
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
                engine.context.value.get<String>("action1") shouldBe "ran"
                engine.context.value.get<Int>("action2") shouldBe 123
            }
        }
    }
})
