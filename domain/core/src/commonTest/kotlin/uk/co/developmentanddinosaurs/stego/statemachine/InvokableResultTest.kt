package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe

class InvokableResultTest : BehaviorSpec({
    Given("an InvokableResult.Success") {
        and("it is created with default values") {
            val result = InvokableResult.Success()

            When("the properties are accessed") {
                Then("the data should be an empty map") {
                    result.data.shouldBeEmpty()
                }
            }
        }

        and("it is created with custom data") {
            val customData = mapOf("key" to "value")
            val result = InvokableResult.Success(data = customData)

            When("the properties are accessed") {
                Then("the data should match the custom data") {
                    result.data shouldBe customData
                }
            }
        }
    }

    Given("an InvokableResult.Failure") {
        and("it is created with default values") {
            val result = InvokableResult.Failure()

            When("the properties are accessed") {
                Then("the data should be an empty map and the cause should be null") {
                    result.data.shouldBeEmpty()
                    result.cause shouldBe null
                }
            }
        }

        and("it is created with custom data and a cause") {
            val customData = mapOf("key" to "value")
            val error = RuntimeException("Something went wrong")
            val result = InvokableResult.Failure(data = customData, cause = error)

            When("the properties are accessed") {
                Then("it should contain the custom data and cause") {
                    result.data shouldBe customData
                    result.cause shouldBe error
                }
            }
        }
    }
})
