package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.AssignActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.StringDataValueDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.OtherActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.AssignActionMapper
import uk.co.developmentanddinosaurs.stego.statemachine.AssignAction

class AssignActionMapperTest : BehaviorSpec() {
  init {
    Given("an AssignActionMapper") {
      val mapper = AssignActionMapper()

      and("an AssignActionDto") {
        val dto =
            AssignActionDto(
                "testKey",
                StringDataValueDto("testValue"),
            )

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
          val exception = shouldThrow<IllegalArgumentException> { mapper.map(dto) }

          Then("it should throw a IllegalArgumentException") {
            exception.message shouldBe "AssignActionMapper can only map AssignActionDto"
          }
        }
      }
    }
  }
}
