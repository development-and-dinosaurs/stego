package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class StateMachineEngineTest : BehaviorSpec({
    Given("a state machine with an initial and a next state") {
        val initialState = State(id = "Initial", on = mapOf("NEXT" to listOf(Transition(target = "Next"))))
        val nextState = State(id = "Next")
        val definition = StateMachineDefinition(
            initial = "Initial", states = mapOf("Initial" to initialState, "Next" to nextState)
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
            id = "Guarded", on = mapOf(
                "EVENT" to listOf(
                    Transition(target = "Other", guard = falseGuard), Transition(target = "Next", guard = trueGuard)
                )
            )
        )
        val definition = StateMachineDefinition(
            initial = "Guarded",
            states = mapOf("Guarded" to guardedState, "Next" to State(id = "Next"), "Other" to State(id = "Other"))
        )

        When("an event is sent that matches a passing guard") {
            val engine = StateMachineEngine(definition)
            engine.send(Event(type = "EVENT"))

            Then("the state machine should transition to the correct state") {
                engine.currentState.value.id shouldBe "Next"
            }
        }
    }
})
