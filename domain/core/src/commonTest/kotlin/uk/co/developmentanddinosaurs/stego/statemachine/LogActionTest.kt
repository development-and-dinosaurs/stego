package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class LogActionTest : BehaviorSpec() {
  init {
    val event = Event("TEST_EVENT")
    val context = Context()

    Given("a logger") {
      var loggedMessage: String? = null
      val logger: (String) -> Unit = { loggedMessage = it }

      and("a LogAction with a literal message") {
        val action = LogAction("Hello, World!", logger)

        When("the action is executed") {
          val newContext = action.execute(context, event)

          Then("it should call the logger with the correct message") {
            loggedMessage shouldBe "LogAction: Hello, World!"
          }

          and("it should return the context unmodified") { newContext shouldBe context }
        }
      }

      and("a LogAction with an empty message") {
        val action = LogAction("", logger)

        When("the action is executed") {
          action.execute(context, event)

          Then("it should call the logger with the correct empty message") {
            loggedMessage shouldBe "LogAction: "
          }
        }
      }
    }
  }
}
