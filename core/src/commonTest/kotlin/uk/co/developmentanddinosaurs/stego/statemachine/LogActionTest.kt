package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class LogActionTest :
    BehaviorSpec({
        val event = Event("TEST_EVENT")
        val context = Context()

        Given("a LogAction with a literal message") {
            var loggedMessage: String? = null
            val logger: (String) -> Unit = { loggedMessage = it }
            val action = LogAction("Hello, World!", logger)

            When("the action is executed") {
                val newContext = action.execute(context, event)

                Then("it should call the logger with the correct message") {
                    loggedMessage shouldBe "LogAction: Hello, World!"
                }

                and("it should return the context unmodified") {
                    newContext shouldBe context
                }
            }
        }
    })
