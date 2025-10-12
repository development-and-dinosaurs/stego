package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ValueTest :
    BehaviorSpec({

        val testEvent = Event(type = "TEST", data = mapOf("eventValue" to "fromEvent"))
        val testContext = Context().put("contextValue", 123)

        Given("a LiteralValue") {
            val literalValue = LiteralValue("hello")

            When("resolve is called") {
                val result = literalValue.resolve(testContext, testEvent)

                Then("it should return the literal value") {
                    result shouldBe "hello"
                }
            }
        }

        Given("a ContextValue") {
            val contextValue = ContextValue<Int>("contextValue")

            When("resolve is called for an existing key") {
                val result = contextValue.resolve(testContext, testEvent)

                Then("it should return the value from the context") {
                    result shouldBe 123
                }
            }

            When("resolve is called for a non-existent key") {
                val missingValue = ContextValue<Any?>("missing")
                val result = missingValue.resolve(testContext, testEvent)

                Then("it should return null") {
                    result shouldBe null
                }
            }
        }

        Given("an EventValue") {
            val eventValue = EventValue<String>("eventValue")

            When("resolve is called for an existing key") {
                val result = eventValue.resolve(testContext, testEvent)

                Then("it should return the value from the event data") {
                    result shouldBe "fromEvent"
                }
            }

            When("resolve is called for a non-existent key") {
                val missingValue = EventValue<String>("missing")
                val result = missingValue.resolve(testContext, testEvent)

                Then("it should return null") {
                    result shouldBe null
                }
            }
        }
    })
