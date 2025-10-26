package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.statemachine.AssignAction

private data class OtherActionDto(val data: String) : ActionDto

class AssignActionMapperTest : BehaviorSpec({
    Given("an AssignActionMapper") {
        val mapper = AssignActionMapper()

        and("an AssignActionDto") {
            val dto = AssignActionDto("testKey", StringDataValueDto("testValue"))

            When("the dto is mapped") {
                val action = mapper.map(dto)

                Then("it should return a corresponding AssignAction") {
                    action.shouldBeInstanceOf<AssignAction>()
                }
            }
        }

        and("a non-AssignActionDto") {
            val dto = OtherActionDto("some data")

            When("the dto is mapped") {
                val exception = shouldThrow<IllegalArgumentException> {
                    mapper.map(dto)
                }

                Then("it should throw a IllegalArgumentException") {
                    exception.message shouldBe "AssignActionMapper can only map AssignActionDto"
                }
            }
        }
    }
})