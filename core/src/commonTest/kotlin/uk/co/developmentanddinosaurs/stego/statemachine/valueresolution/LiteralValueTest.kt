package uk.co.developmentanddinosaurs.stego.statemachine.valueresolution

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

class LiteralValueTest : BehaviorSpec({
    val context = Context()
    val event = Event("TEST_EVENT")

    Given("a LiteralValue provider with a string value") {
        val provider = LiteralValue("myValue")

        When("the value is retrieved") {
            val result = provider.get(context, event)

            Then("it should return the string value") {
                result shouldBe "myValue"
            }
        }
    }

    Given("a LiteralValue provider with an integer value") {
        val provider = LiteralValue(123)

        When("the value is retrieved") {
            val result = provider.get(context, event)

            Then("it should return the integer value") {
                result shouldBe 123
            }
        }
    }

    Given("a LiteralValue provider with a null value") {
        val provider = LiteralValue(null)

        When("the value is retrieved") {
            val result = provider.get(context, event)

            Then("it should return null") {
                result shouldBe null
            }
        }
    }
})