package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ValueResolverTest : BehaviorSpec({

    // Test data
    val context = Context().put("user", ObjectValue(mapOf("id" to StringPrimitive("user123"))))
    val event = Event("TEST_EVENT", mapOf("payload" to StringPrimitive("event_data")))

    Given("the ValueResolver.parse function") {
        When("parsing a context reference string") {
            val result = ValueResolver.parse("context.user.id")
            Then("it should return a ContextReference") {
                result shouldBe ContextReference("user.id")
            }
        }

        When("parsing an event reference string") {
            val result = ValueResolver.parse("event.payload")
            Then("it should return an EventReference") {
                result shouldBe EventReference("payload")
            }
        }

        When("parsing a literal string") {
            val result = ValueResolver.parse("just a literal string")
            Then("it should return a StringPrimitive") {
                result shouldBe StringPrimitive("just a literal string")
            }
        }
    }

    // Tests for ValueResolver.resolve()
    Given("the ValueResolver.resolve function") {
        When("resolving a ContextReference") {
            val reference = ContextReference("user.id")
            val result = ValueResolver.resolve(reference, context, event)
            Then("it should return the value from the context") {
                result shouldBe StringPrimitive("user123")
            }
        }

        When("resolving an EventReference") {
            val reference = EventReference("payload")
            val result = ValueResolver.resolve(reference, context, event)
            Then("it should return the value from the event") {
                result shouldBe StringPrimitive("event_data")
            }
        }

        When("resolving a LiteralReference") {
            val reference = LiteralReference(LongPrimitive(999))
            val result = ValueResolver.resolve(reference, context, event)
            Then("it should return the literal value") {
                result shouldBe LongPrimitive(999)
            }
        }

        When("resolving a concrete DataValue") {
            val value = BooleanPrimitive(true)
            val result = ValueResolver.resolve(value, context, event)
            Then("it should return the value itself") {
                result shouldBe value
            }
        }
    }
})