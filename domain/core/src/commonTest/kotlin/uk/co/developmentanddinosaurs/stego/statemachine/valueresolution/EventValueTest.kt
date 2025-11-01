package uk.co.developmentanddinosaurs.stego.statemachine.valueresolution

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

class EventValueTest : BehaviorSpec({
    val context = Context()

    Given("an EventValue provider") {
        val provider = EventValue("myKey")

        and("an event containing the key") {
            val event = Event("TEST", mapOf("myKey" to "myValue"))

            When("the value is retrieved") {
                val result = provider.get(context, event)

                Then("it should return the correct value from the event") {
                    result shouldBe "myValue"
                }
            }
        }

        and("an event that does not contain the key") {
            val event = Event("TEST", mapOf("otherKey" to "otherValue"))

            When("the value is retrieved") {
                val result = provider.get(context, event)

                Then("it should return null") {
                    result shouldBe null
                }
            }
        }

        and("a null event") {
            When("the value is retrieved") {
                val result = provider.get(context, null)

                Then("it should return null") {
                    result shouldBe null
                }
            }
        }
    }
})
