package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class AssignActionTest :
    BehaviorSpec({
      val event = Event("TEST_EVENT")

      Given("an AssignAction with a literal value") {
        val action = AssignAction("myKey", "myValue")

        and("an initial context") {
          val context = Context()

          When("the action is executed") {
            val newContext = action.execute(context, event)

            Then("the new context should contain the literal value") {
              newContext.get("myKey") shouldBe "myValue"
            }
          }
        }
      }

      Given("an AssignAction with a context value expression") {
        val action = AssignAction("targetKey", "{context.sourceKey}")

        and("a context containing the source key") {
          val context = Context().put("sourceKey", "resolvedValue")

          When("the action is executed") {
            val newContext = action.execute(context, event)

            Then("the new context should contain the resolved value") {
              newContext.get("targetKey") shouldBe "resolvedValue"
            }
          }
        }
      }

      Given("an AssignAction with an event value expression") {
        val action = AssignAction("targetKey", "{event.sourceKey}")

        and("an event containing the source key") {
          val context = Context()
          val eventWithData = Event("TEST", mapOf("sourceKey" to "eventValue"))

          When("the action is executed") {
            val newContext = action.execute(context, eventWithData)

            Then("the new context should contain the resolved value") {
              newContext.get("targetKey") shouldBe "eventValue"
            }
          }
        }
      }

      Given("an AssignAction") {
        val action = AssignAction("myKey", "newValue")

        and("a context that already contains the target key") {
          val context = Context().put("myKey", "oldValue")

          When("the action is executed") {
            val newContext = action.execute(context, event)

            Then("the value in the new context should be overwritten") {
              newContext.get("myKey") shouldBe "newValue"
            }
          }
        }
      }
    })
