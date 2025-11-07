package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.serialisation.InvokableDefinitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.BooleanDataValueDto
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.NumberDataValueDto
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.StringDataValueDto
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.InvokableDefinitionMapper
import uk.co.developmentanddinosaurs.stego.statemachine.Invokable
import uk.co.developmentanddinosaurs.stego.statemachine.InvokableResult
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException

class InvokableDefinitionMapperTest : BehaviorSpec() {
  init {
    val dummyInvokable = Invokable { InvokableResult.Success() }
    val registry = mapOf("my-invoke" to dummyInvokable)
    val mapper = InvokableDefinitionMapper(registry)

    Given("an InvokableDefinitionDto with various inputs") {
      val dto =
          InvokableDefinitionDto(
              id = "testId",
              src = "my-invoke",
              input =
                  mapOf(
                      "literalString" to StringDataValueDto("hello"),
                      "literalLong" to NumberDataValueDto(123L),
                      "literalDouble" to NumberDataValueDto(45.6),
                      "literalBoolean" to BooleanDataValueDto(true),
                  ),
          )

      When("the mapper maps the DTO") {
        val definition = mapper.map(dto)

        Then("it should return a correctly mapped InvokableDefinition") {
          definition.id shouldBe "testId"
          definition.src shouldBe dummyInvokable
          definition.input shouldContainExactly
              mapOf<String, Any?>(
                  "literalString" to "hello",
                  "literalLong" to 123L,
                  "literalDouble" to 45.6,
                  "literalBoolean" to true,
              )
        }
      }
    }

    Given("an InvokableDefinitionDto with no input") {
      val dto = InvokableDefinitionDto(id = "testId", src = "my-invoke", input = emptyMap())

      When("the mapper maps the DTO") {
        val definition = mapper.map(dto)

        Then("the resulting definition should have an empty input map") {
          definition.input.isEmpty() shouldBe true
        }
      }
    }

    Given("an InvokableDefinitionDto with an unknown src") {
      val dto = InvokableDefinitionDto(id = "testId", src = "unknown-invoke")

      When("the mapper maps the DTO") {
        Then("it should throw a StateMachineException") {
          val exception = shouldThrow<StateMachineException> { mapper.map(dto) }
          exception.message shouldBe "Invokable source 'unknown-invoke' not found in registry."
        }
      }
    }
  }
}
