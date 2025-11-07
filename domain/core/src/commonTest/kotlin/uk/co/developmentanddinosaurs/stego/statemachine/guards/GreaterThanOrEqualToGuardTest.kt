package uk.co.developmentanddinosaurs.stego.statemachine.guards

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.valueresolution.ValueProvider

class GreaterThanOrEqualToGuardTest : BehaviorSpec() {
  init {
    val context = Context()
    val event = Event("TEST_EVENT")

    Given("the left value is greater than the right value") {
      val leftProvider = ValueProvider.resolve(10)
      val rightProvider = ValueProvider.resolve(5)

      When("the guard is evaluated") {
        val guard = GreaterThanOrEqualToGuard(leftProvider, rightProvider)
        val result = guard.evaluate(context, event)

        Then("it should return true") { result shouldBe true }
      }
    }

    Given("the left value is equal to the right value") {
      val leftProvider = ValueProvider.resolve(5)
      val rightProvider = ValueProvider.resolve(5)

      When("the guard is evaluated") {
        val guard = GreaterThanOrEqualToGuard(leftProvider, rightProvider)
        val result = guard.evaluate(context, event)

        Then("it should return true") { result shouldBe true }
      }
    }

    Given("the left value is less than the right value") {
      val leftProvider = ValueProvider.resolve(1)
      val rightProvider = ValueProvider.resolve(5)

      When("the guard is evaluated") {
        val guard = GreaterThanOrEqualToGuard(leftProvider, rightProvider)
        val result = guard.evaluate(context, event)

        Then("it should return false") { result shouldBe false }
      }
    }
  }
}
