package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.LogAction
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException

class ActionMapperTest : BehaviorSpec({

    var loggedMessage: String?
    val testLogger: (String) -> Unit = { message -> loggedMessage = message }

    Given("a LogActionMapper") {
        val mapper = LogActionMapper(testLogger)

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
        val compositeMapper = CompositeActionMapper(
            mapOf(
                LogActionDto::class to LogActionMapper(testLogger),
                AssignActionDto::class to AssignActionMapper()
            )
        )

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
            val dummyDto = object : ActionDto {}

            Then("it should throw a StateMachineException") {
                shouldThrow<StateMachineException> {
                    compositeMapper.map(dummyDto)
                }
            }
        }
    }
})
