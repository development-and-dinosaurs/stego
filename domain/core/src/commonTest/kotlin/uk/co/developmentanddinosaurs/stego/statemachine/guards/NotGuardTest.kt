package uk.co.developmentanddinosaurs.stego.statemachine.guards

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

class NotGuardTest : BehaviorSpec() {
  init {
    val context = Context()
    val event = Event("TEST_EVENT")

    Given("the inner guard is true") {
      When("the NotGuard is evaluated") {
        val guard = NotGuard(TrueGuard)
        val result = guard.evaluate(context, event)

        Then("it should return false") { result shouldBe false }
      }
    }

    Given("the inner guard is false") {
      When("the NotGuard is evaluated") {
        val guard = NotGuard(FalseGuard)
        val result = guard.evaluate(context, event)

        Then("it should return true") { result shouldBe true }
      }
    }
  }
}
