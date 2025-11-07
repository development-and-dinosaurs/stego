package uk.co.developmentanddinosaurs.stego.statemachine.guards

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

class OrGuardTest : BehaviorSpec() {
  init {
    val context = Context()
    val event = Event("TEST_EVENT")

    Given("all inner guards are true") {
      When("the OrGuard is evaluated") {
        val guard = OrGuard(TrueGuard, TrueGuard)
        val result = guard.evaluate(context, event)

        Then("it should return true") { result shouldBe true }
      }
    }

    Given("some inner guards are true") {
      When("the OrGuard is evaluated") {
        val guard = OrGuard(FalseGuard, TrueGuard)
        val result = guard.evaluate(context, event)

        Then("it should return true") { result shouldBe true }
      }
    }

    Given("all inner guards are false") {
      When("the OrGuard is evaluated") {
        val guard = OrGuard(FalseGuard, FalseGuard)
        val result = guard.evaluate(context, event)

        Then("it should return false") { result shouldBe false }
      }
    }

    Given("there are no inner guards") {
      When("the OrGuard is evaluated") {
        val guard = OrGuard()
        val result = guard.evaluate(context, event)

        Then("it should return false") { result shouldBe false }
      }
    }
  }
}
