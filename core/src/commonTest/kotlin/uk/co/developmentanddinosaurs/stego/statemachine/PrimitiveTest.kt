package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class PrimitiveTest : BehaviorSpec({

    Given("two StringPrimitives") {
        val stringA = StringPrimitive("a")
        val stringB = StringPrimitive("b")

        And("a is less than b") {
            When("comparing them") {
                Then("a < b should be true") {
                    (stringA < stringB) shouldBe true
                }
                Then("b > a should be true") {
                    (stringB > stringA) shouldBe true
                }
                Then("a == b should be false") {
                    (stringA == stringB) shouldBe false
                }
            }
        }

        And("a is equal to a") {
            When("comparing them") {
                Then("a < a should be false") {
                    (stringA < stringA) shouldBe false
                }
                Then("a > a should be false") {
                    (stringA > stringA) shouldBe false
                }
                Then("a == a should be true") {
                    (stringA == stringA) shouldBe true
                }
            }
        }
    }

    Given("two NumericPrimitives of different types") {
        val longPrimitive = LongPrimitive(100)
        val doublePrimitive = DoublePrimitive(100.5)

        And("long is less than double") {
            When("comparing them") {
                Then("long < double should be true") {
                    (longPrimitive < doublePrimitive) shouldBe true
                }
                Then("double > long should be true") {
                    (doublePrimitive > longPrimitive) shouldBe true
                }
            }
        }
    }

    Given("two BooleanPrimitives") {
        val truePrimitive = BooleanPrimitive(true)
        val falsePrimitive = BooleanPrimitive(false)

        And("false is less than true") {
            When("comparing them") {
                Then("false < true should be true") {
                    (falsePrimitive < truePrimitive) shouldBe true
                }
            }
        }
    }

    Given("two incompatible Primitive types") {
        val stringPrimitive = StringPrimitive("hello")
        val booleanPrimitive = BooleanPrimitive(true)

        When("compareTo is called") {
            Then("it should throw an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    stringPrimitive > booleanPrimitive
                }
                shouldThrow<IllegalArgumentException> {
                    booleanPrimitive < stringPrimitive
                }
            }
        }
    }
})
