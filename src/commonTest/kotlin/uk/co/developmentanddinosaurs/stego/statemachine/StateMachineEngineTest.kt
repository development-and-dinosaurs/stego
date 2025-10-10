package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

private data class TestEvent(override val type: String, override val data: Map<String, Any> = emptyMap()) : Event

private data class TestTransition(
    override val target: String,
    override val actions: List<Action> = emptyList(),
    override val guard: Guard? = null
) : Transition

private data class TestState(
    override val id: String,
    override val on: Map<String, List<Transition>> = emptyMap(),
    override val onEntry: List<Action> = emptyList(),
    override val onExit: List<Action> = emptyList(),
    override val invoke: Invokable? = null,
    override val initial: String? = null,
    override val states: Map<String, State>? = null
) : State

private data class TestStateMachineDefinition(
    override val initial: String,
    override val states: Map<String, State>,
    override val initialContext: Map<String, Any> = emptyMap()
) : StateMachineDefinition

class StateMachineEngineTest : BehaviorSpec({
    Given("a state machine with an initial and a next state") {
        val initialState = TestState("Initial", on = mapOf("NEXT" to listOf(TestTransition("Next"))))
        val definition = TestStateMachineDefinition(
            initial = "Initial",
            states = mapOf("Initial" to initialState, "Next" to TestState("Next"))
        )

        When("the engine is created") {
            val engine = StateMachineEngine(definition)

            Then("the initial state should be correct") {
                engine.currentState.value shouldBe initialState
            }
        }

        When("an event is sent that triggers a transition") {
            val engine = StateMachineEngine(definition)
            engine.send(TestEvent("NEXT"))

            Then("the state machine should transition to the next state") {
                engine.currentState.value shouldBe TestState("Next")
            }
        }

        When("an event is sent that does not trigger a transition") {
            val engine = StateMachineEngine(definition)
            engine.send(TestEvent("UNKNOWN"))

            Then("the state machine should remain in the initial state") {
                engine.currentState.value shouldBe initialState
            }
        }
    }
})
