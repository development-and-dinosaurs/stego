package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class GuardTest : BehaviorSpec({

    val testEvent = Event(type = "TEST", data = mapOf("isTest" to true, "count" to 5))
    val testContext = Context().put("userId", 123)
    val trueGuard = EqualsGuard(LiteralValue(1), LiteralValue(1))
    val falseGuard = EqualsGuard(LiteralValue(1), LiteralValue(2))

    Given("an EqualsGuard") {
        When("it compares a context value to a matching literal") {
            val guard = EqualsGuard(ContextValue("userId"), LiteralValue(123))
            Then("it should evaluate to true") {
                guard.evaluate(testContext, testEvent) shouldBe true
            }
        }
        When("it compares an event value to a non-matching literal") {
            val guard = EqualsGuard(EventValue("isTest"), LiteralValue(false))
            Then("it should evaluate to false") {
                guard.evaluate(testContext, testEvent) shouldBe false
            }
        }
    }

    Given("a NotEqualsGuard") {
        When("it compares a context value to a non-matching literal") {
            val guard = NotEqualsGuard(ContextValue("userId"), LiteralValue(456))
            Then("it should evaluate to true") {
                guard.evaluate(testContext, testEvent) shouldBe true
            }
        }
        When("it compares an event value to a matching literal") {
            val guard = NotEqualsGuard(EventValue("isTest"), LiteralValue(true))
            Then("it should evaluate to false") {
                guard.evaluate(testContext, testEvent) shouldBe false
            }
        }
    }

    Given("a GreaterThanGuard") {
        When("the left value is greater than the right value") {
            val guard = GreaterThanGuard(EventValue("count"), LiteralValue(4))
            Then("it should evaluate to true") {
                guard.evaluate(testContext, testEvent) shouldBe true
            }
        }
        When("the left value is not greater than the right value") {
            val guard = GreaterThanGuard(EventValue("count"), LiteralValue(5))
            Then("it should evaluate to false") {
                guard.evaluate(testContext, testEvent) shouldBe false
            }
        }
    }

    Given("a LessThanGuard") {
        When("the left value is less than the right value") {
            val guard = LessThanGuard(EventValue("count"), LiteralValue(6))
            Then("it should evaluate to true") {
                guard.evaluate(testContext, testEvent) shouldBe true
            }
        }
        When("the left value is not less than the right value") {
            val guard = LessThanGuard(EventValue("count"), LiteralValue(5))
            Then("it should evaluate to false") {
                guard.evaluate(testContext, testEvent) shouldBe false
            }
        }
        When("the values are not comparable") {
            val guard = LessThanGuard(EventValue("isTest"), LiteralValue(5))
            Then("it should evaluate to false") {
                guard.evaluate(testContext, testEvent) shouldBe false
            }
        }
    }

    Given("a GreaterThanOrEqualsGuard") {
        When("the left value is greater than the right value") {
            val guard = GreaterThanOrEqualsGuard(EventValue("count"), LiteralValue(4))
            Then("it should evaluate to true") {
                guard.evaluate(testContext, testEvent) shouldBe true
            }
        }
        When("the left value is equal to the right value") {
            val guard = GreaterThanOrEqualsGuard(EventValue("count"), LiteralValue(5))
            Then("it should evaluate to true") {
                guard.evaluate(testContext, testEvent) shouldBe true
            }
        }
        When("the left value is less than the right value") {
            val guard = GreaterThanOrEqualsGuard(EventValue("count"), LiteralValue(6))
            Then("it should evaluate to false") {
                guard.evaluate(testContext, testEvent) shouldBe false
            }
        }
    }

    Given("a LessThanOrEqualsGuard") {
        When("the left value is less than the right value") {
            val guard = LessThanOrEqualsGuard(EventValue("count"), LiteralValue(6))
            Then("it should evaluate to true") {
                guard.evaluate(testContext, testEvent) shouldBe true
            }
        }
        When("the left value is equal to the right value") {
            val guard = LessThanOrEqualsGuard(EventValue("count"), LiteralValue(5))
            Then("it should evaluate to true") {
                guard.evaluate(testContext, testEvent) shouldBe true
            }
        }
        When("the left value is greater than the right value") {
            val guard = LessThanOrEqualsGuard(EventValue("count"), LiteralValue(4))
            Then("it should evaluate to false") {
                guard.evaluate(testContext, testEvent) shouldBe false
            }
        }
    }

    Given("an AndGuard") {
        When("all sub-guards are true") {
            val andGuard = AndGuard(listOf(trueGuard, trueGuard))
            Then("it should evaluate to true") {
                andGuard.evaluate(testContext, testEvent) shouldBe true
            }
        }

        When("one sub-guard is false") {
            val andGuard = AndGuard(listOf(trueGuard, falseGuard))
            Then("it should evaluate to false") {
                andGuard.evaluate(testContext, testEvent) shouldBe false
            }
        }
    }

    Given("an OrGuard") {
        When("one sub-guard is true") {
            val orGuard = OrGuard(listOf(trueGuard, falseGuard))
            Then("it should evaluate to true") {
                orGuard.evaluate(testContext, testEvent) shouldBe true
            }
        }

        When("all sub-guards are false") {
            val orGuard = OrGuard(listOf(falseGuard, falseGuard))
            Then("it should evaluate to false") {
                orGuard.evaluate(testContext, testEvent) shouldBe false
            }
        }
    }

    Given("a NotGuard") {
        When("the sub-guard is true") {
            val notGuard = NotGuard(trueGuard)
            Then("it should evaluate to false") {
                notGuard.evaluate(testContext, testEvent) shouldBe false
            }
        }

        When("the sub-guard is false") {
            val notGuard = NotGuard(falseGuard)
            Then("it should evaluate to true") {
                notGuard.evaluate(testContext, testEvent) shouldBe true
            }
        }
    }
})
