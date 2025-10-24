package uk.co.developmentanddinosaurs.stego.statemachine.valueresolution

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

class ValueProviderTest :
    BehaviorSpec({
        Given("a ValueProvider") {
            val context = Context().put("myKey", 123)
            val event = Event("TEST", mapOf("myKey" to "abc"))

            When("resolving a context value") {
                val provider = ValueProvider.resolve("{context.myKey}")

                Then("it should create a ContextValue provider") {
                    provider.shouldBeInstanceOf<ContextValue>()
                }

                Then("it should return the correct value from the context") {
                    provider.get(context, event) shouldBe 123
                }
            }

            When("resolving an event value") {
                val provider = ValueProvider.resolve("{event.myKey}")

                Then("it should create an EventValue provider") {
                    provider.shouldBeInstanceOf<EventValue>()
                }

                Then("it should return the correct value from the event") {
                    provider.get(context, event) shouldBe "abc"
                }
            }

            When("resolving an integer literal") {
                val provider = ValueProvider.resolve("42")

                Then("it should create a LiteralValue provider") {
                    provider.shouldBeInstanceOf<LiteralValue>()
                }

                Then("it should return the correct integer value") {
                    provider.get(context, event) shouldBe 42
                }
            }

            When("resolving a double literal") {
                val provider = ValueProvider.resolve("3.14")

                Then("it should create a LiteralValue provider") {
                    provider.shouldBeInstanceOf<LiteralValue>()
                }

                Then("it should return the correct double value") {
                    provider.get(context, event) shouldBe 3.14
                }
            }

            When("resolving a boolean 'true' literal") {
                val provider = ValueProvider.resolve("true")

                Then("it should return the correct boolean value") {
                    provider.get(context, event) shouldBe true
                }
            }

            When("resolving a boolean 'false' literal") {
                val provider = ValueProvider.resolve("false")

                Then("it should return the correct boolean value") {
                    provider.get(context, event) shouldBe false
                }
            }

            When("resolving a quoted string literal") {
                val provider = ValueProvider.resolve("\"hello world\"")

                Then("it should return the unquoted string value") {
                    provider.get(context, event) shouldBe "hello world"
                }
            }

            When("resolving an unquoted string literal") {
                val provider = ValueProvider.resolve("a_simple_string")

                Then("it should return the string value as is") {
                    provider.get(context, event) shouldBe "a_simple_string"
                }
            }

            When("resolving a non-string value") {
                val provider = ValueProvider.resolve(999)

                Then("it should return the value directly") {
                    provider.get(context, event) shouldBe 999
                }
            }
        }
    })
