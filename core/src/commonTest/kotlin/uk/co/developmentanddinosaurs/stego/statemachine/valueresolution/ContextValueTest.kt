package uk.co.developmentanddinosaurs.stego.statemachine.valueresolution

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

class ContextValueTest : BehaviorSpec({
    val event = Event("TEST_EVENT")

    Given("a ContextValue provider") {
        val provider = ContextValue("myKey")

        and("a context containing the key") {
            val context = Context().put("myKey", "myValue")

            When("the value is retrieved") {
                val result = provider.get(context, event)

                Then("it should return the correct value from the context") {
                    result shouldBe "myValue"
                }
            }
        }

        and("a context that does not contain the key") {
            val context = Context().put("otherKey", "otherValue")

            When("the value is retrieved") {
                val result = provider.get(context, event)

                Then("it should return null") {
                    result shouldBe null
                }
            }
        }
    }
})