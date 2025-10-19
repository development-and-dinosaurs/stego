package uk.co.developmentanddinosaurs.stego.app

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import uk.co.developmentanddinosaurs.stego.statemachine.*
import uk.co.developmentanddinosaurs.stego.statemachine.guards.Guard

@ExperimentalCoroutinesApi
class LoginViewModelTest : BehaviorSpec({
    coroutineTestScope = true

    // Define a minimal, self-contained state machine for this test
    val testLoginStateMachineDefinition = StateMachineDefinition(
        initial = "Start",
        states = mapOf(
            "Start" to LogicState(
                id = "Start",
                on = mapOf(
                    "SUBMIT" to listOf(
                        Transition("Success", guard = Guard.create("event.username == 'stego'")),
                        Transition("Error", actions = listOf(AssignAction("error", "Invalid username")))
                    )
                )
            ),
            "Success" to LogicState(id = "Success"),
            "Error" to LogicState(id = "Error")
        )
    )

    Given("a LoginViewModel") {
        val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)

        When("a successful login event is sent") {
            val viewModel = LoginViewModel(testLoginStateMachineDefinition, testDispatcher)
            val event = Event("SUBMIT", mapOf("username" to "stego"))
            viewModel.onEvent(event)
            testCoroutineScheduler.advanceUntilIdle()

            Then("the final state should be Success") {
                viewModel.uiState.value.state.id shouldBe "Success"
            }
        }

        When("a failed login event is sent") {
            val viewModel = LoginViewModel(testLoginStateMachineDefinition, testDispatcher)
            val event = Event("SUBMIT", mapOf("username" to "wrong"))
            viewModel.onEvent(event)
            testCoroutineScheduler.advanceUntilIdle()

            Then("the final state should be Error") {
                viewModel.uiState.value.state.id shouldBe "Error"
            }

            And("the context should contain an error message") {
                viewModel.uiState.value.context.get("error") shouldBe "Invalid username"
            }
        }
    }
})
