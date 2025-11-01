package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.LogActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.OtherActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.LogActionMapper
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.LogAction

class LogActionMapperTest : BehaviorSpec({
    Given("a LogActionMapper") {
        var loggedMessage: String? = null
        val testLogger: (String) -> Unit = { loggedMessage = it }
        val mapper = LogActionMapper(testLogger)

        and("a LogActionDto") {
            val dto = LogActionDto("test message")

            When("the dto is mapped") {
                val action = mapper.map(dto)

                Then("it should return a corresponding LogAction") {
                    action.shouldBeInstanceOf<LogAction>()
                }

                and("the action should use the injected logger") {
                    action.execute(Context(), Event("TEST"))
                    loggedMessage shouldBe "LogAction: test message"
                }
            }
        }

        and("a non-LogActionDto") {
            val dto = OtherActionDto("some data")

            When("the dto is mapped") {
                Then("it should throw an IllegalArgumentException") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            mapper.map(dto)
                        }
                    exception.message shouldBe "LogActionMapper can only map LogActionDto"
                }
            }
        }
    }
})
