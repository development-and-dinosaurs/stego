package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class GuardTest :
    BehaviorSpec({

        val testEvent = Event(type = "TEST")
        val testContext = Context()
        val valueOne = LiteralValue<Any>(1)
        val valueTwo = LiteralValue<Any>(2)
        val trueGuard = EqualsGuard(valueOne, valueOne)
        val falseGuard = EqualsGuard(valueOne, valueTwo)

        Given("an EqualsGuard") {
            And("the left value is equal to the right value") {
                val guard = EqualsGuard(valueOne, valueOne)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
            And("the left value is not equal to the right value") {
                val guard = EqualsGuard(valueOne, valueTwo)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to false") {
                        result shouldBe false
                    }
                }
            }
        }

        Given("a NotEqualsGuard") {
            And("the left value is not equal to the right value") {
                val guard = NotEqualsGuard(valueOne, valueTwo)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
            And("the left value is equal to the right value") {
                val guard = NotEqualsGuard(valueOne, valueOne)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to false") {
                        result shouldBe false
                    }
                }
            }
        }

        Given("a GreaterThanGuard") {
            And("the left value is greater than the right value") {
                val guard = GreaterThanGuard(valueTwo, valueOne)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
            And("the left value is not greater than the right value") {
                val guard = GreaterThanGuard(valueOne, valueTwo)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to false") {
                        result shouldBe false
                    }
                }
            }
        }

        Given("a LessThanGuard") {
            And("the left value is less than the right value") {
                val guard = LessThanGuard(valueOne, valueTwo)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
            And("the left value is not less than the right value") {
                val guard = LessThanGuard(valueTwo, valueOne)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to false") {
                        result shouldBe false
                    }
                }
            }
        }

        Given("a GreaterThanOrEqualsGuard") {
            And("the left value is greater than the right value") {
                val guard = GreaterThanOrEqualsGuard(valueTwo, valueOne)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
            And("the left value is equal to the right value") {
                val guard = GreaterThanOrEqualsGuard(valueOne, valueOne)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
            And("the left value is less than the right value") {
                val guard = GreaterThanOrEqualsGuard(valueOne, valueTwo)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to false") {
                        result shouldBe false
                    }
                }
            }
        }

        Given("a LessThanOrEqualsGuard") {
            And("the left value is less than the right value") {
                val guard = LessThanOrEqualsGuard(valueOne, valueTwo)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
            And("the left value is equal to the right value") {
                val guard = LessThanOrEqualsGuard(valueOne, valueOne)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
            And("the left value is greater than the right value") {
                val guard = LessThanOrEqualsGuard(valueTwo, valueOne)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to false") {
                        result shouldBe false
                    }
                }
            }
        }

        Given("an AndGuard") {
            And("all sub-guards are true") {
                val guard = AndGuard(listOf(trueGuard, trueGuard))
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
            And("one sub-guard is false") {
                val guard = AndGuard(listOf(trueGuard, falseGuard))
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to false") {
                        result shouldBe false
                    }
                }
            }
        }

        Given("an OrGuard") {
            And("one sub-guard is true") {
                val guard = OrGuard(listOf(trueGuard, falseGuard))
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
            And("all sub-guards are false") {
                val guard = OrGuard(listOf(falseGuard, falseGuard))
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to false") {
                        result shouldBe false
                    }
                }
            }
        }

        Given("a NotGuard") {
            And("the sub-guard is true") {
                val guard = NotGuard(trueGuard)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to false") {
                        result shouldBe false
                    }
                }
            }
            And("the sub-guard is false") {
                val guard = NotGuard(falseGuard)
                When("the guard is evaluated") {
                    val result = guard.evaluate(testContext, testEvent)
                    Then("it should evaluate to true") {
                        result shouldBe true
                    }
                }
            }
        }
    })
