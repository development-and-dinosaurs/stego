package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.LogAction

class ActionMapperTest : BehaviorSpec({

    var loggedMessage: String?
    val testLogger: (String) -> Unit = { message -> loggedMessage = message }

    Given("a LogActionMapper") {
        val mapper = LogActionMapper(testLogger)

        When("checking if it can map a LogActionDto") {
            val dto = LogActionDto("Test Message")
            Then("it should return true") {
                mapper.canMap(dto).shouldBeTrue()
            }
        }

        When("checking if it can map a different ActionDto type") {
            val dummyDto = object : ActionDto {
                override fun toDomain(): Action {
                    TODO("Not yet implemented")
                }
            }
            Then("it should return false") {
                mapper.canMap(dummyDto).shouldBeFalse()
            }
        }

        When("mapping a LogActionDto") {
            val dto = LogActionDto("Hello from test!")
            val action = mapper.map(dto)

            Then("it should return a LogAction") {
                action.shouldBeInstanceOf<LogAction>()
            }

            Then("the LogAction should use the logger when executed") {
                loggedMessage = null // Reset logger for this test
                action.execute(Context(), Event("TEST_EVENT"))
                loggedMessage shouldBe "LogAction: Hello from test!"
            }
        }
    }

    Given("a CompositeActionMapper with a LogActionMapper") {
        val logMapper = LogActionMapper(testLogger)
        val compositeMapper = CompositeActionMapper(listOf(logMapper))

        When("checking if it can map a LogActionDto") {
            val dto = LogActionDto("Composite Test")
            Then("it should return true") {
                compositeMapper.canMap(dto).shouldBeTrue()
            }
        }

        When("mapping a LogActionDto") {
            val dto = LogActionDto("Composite Map Test")
            val action = compositeMapper.map(dto)

            Then("it should return a LogAction") {
                action.shouldBeInstanceOf<LogAction>()
            }

            Then("the LogAction should use the logger when executed") {
                loggedMessage = null // Reset logger for this test
                action.execute(Context(), Event("COMPOSITE_EVENT"))
                loggedMessage shouldBe "LogAction: Composite Map Test"
            }
        }

        When("mapping an unknown ActionDto type") {
            val dummyDto = object : ActionDto {
                override fun toDomain(): Action {
                    TODO("Not yet implemented")
                }
            }
            Then("it should return false for canMap") {
                compositeMapper.canMap(dummyDto).shouldBeFalse()
            }
            Then("it should throw an IllegalArgumentException") {
                shouldThrow<IllegalArgumentException> {
                    compositeMapper.map(dummyDto)
                }
            }
        }
    }
})
