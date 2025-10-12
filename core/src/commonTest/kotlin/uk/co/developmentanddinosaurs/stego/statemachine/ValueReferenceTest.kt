package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ValueReferenceTest : BehaviorSpec({

    val testEvent = Event(type = "TEST", data = mapOf("eventValue" to StringPrimitive("fromEvent")))
    val testContext = Context().put("contextValue", LongPrimitive(123))

    Given("a LiteralReference") {
        val literalReference = LiteralReference(StringPrimitive("hello"))

        When("resolve is called") {
            val result = literalReference.resolve(testContext, testEvent)

            Then("it should return the literal value") {
                result shouldBe StringPrimitive("hello")
            }
        }
    }

    Given("a ContextReference") {
        val contextReference = ContextReference("contextValue")

        When("resolve is called for an existing key") {
            val result = contextReference.resolve(testContext, testEvent)

            Then("it should return the value from the context") {
                result shouldBe LongPrimitive(123)
            }
        }

        When("resolve is called for a non-existent key") {
            val missingReference = ContextReference("missing")
            val result = missingReference.resolve(testContext, testEvent)

            Then("it should return null") {
                result shouldBe null
            }
        }
    }

    Given("an EventReference") {
        val eventReference = EventReference("eventValue")

        When("resolve is called for an existing key") {
            val result = eventReference.resolve(testContext, testEvent)

            Then("it should return the value from the event data") {
                result shouldBe StringPrimitive("fromEvent")
            }
        }

        When("resolve is called for a non-existent key") {
            val missingReference = EventReference("missing")
            val result = missingReference.resolve(testContext, testEvent)

            Then("it should return null") {
                result shouldBe null
            }
        }
    }
})
