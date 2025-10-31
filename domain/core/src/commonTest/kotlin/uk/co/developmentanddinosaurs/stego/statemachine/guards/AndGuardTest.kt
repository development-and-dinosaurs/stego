package uk.co.developmentanddinosaurs.stego.statemachine.guards

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

class AndGuardTest :
    BehaviorSpec({
        val context = Context()
        val event = Event("TEST_EVENT")

        Given("all inner guards are true") {
            When("the AndGuard is evaluated") {
                val guard = AndGuard(TrueGuard, TrueGuard)
                val result = guard.evaluate(context, event)

                Then("it should return true") {
                    result shouldBe true
                }
            }
        }

        Given("some inner guards are false") {
            When("the AndGuard is evaluated") {
                val guard = AndGuard(TrueGuard, FalseGuard)
                val result = guard.evaluate(context, event)

                Then("it should return false") {
                    result shouldBe false
                }
            }
        }

        Given("all inner guards are false") {
            When("the AndGuard is evaluated") {
                val guard = AndGuard(FalseGuard, FalseGuard)
                val result = guard.evaluate(context, event)

                Then("it should return false") {
                    result shouldBe false
                }
            }
        }

        Given("there are no inner guards") {
            When("the AndGuard is evaluated") {
                val guard = AndGuard()
                val result = guard.evaluate(context, event)

                Then("it should return true") {
                    result shouldBe true
                }
            }
        }
    })
