package uk.co.developmentanddinosaurs.stego.app

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.StringPrimitive

@ExperimentalCoroutinesApi
class LoginViewModelTest : BehaviorSpec({
    coroutineTestScope = true

    Given("a LoginViewModel") {
        val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)

        When("a successful login event is sent") {
            val viewModel = LoginViewModel(testDispatcher)
            val event = Event("SUBMIT", mapOf("username" to StringPrimitive("stego")))
            viewModel.onEvent(event)
            testCoroutineScheduler.advanceUntilIdle()

            Then("the final state should be Success") {
                viewModel.uiState.value.state.id shouldBe "Success"
            }
        }

        When("a failed login event is sent") {
            val viewModel = LoginViewModel(testDispatcher)
            val event = Event("SUBMIT", mapOf("username" to StringPrimitive("wrong")))
            viewModel.onEvent(event)
            testCoroutineScheduler.advanceUntilIdle()

            Then("the final state should be Error") {
                viewModel.uiState.value.state.id shouldBe "Error"
            }

            And("the context should contain an error message") {
                (viewModel.uiState.value.context.get("error") as? StringPrimitive)?.value shouldBe "Invalid username"
            }
        }
    }
})
