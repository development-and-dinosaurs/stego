package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.CompositeValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.ui.validators.ValidationResult
import uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule

private data class TestValidationRuleDto(
    override val message: String,
) : ValidationRuleDto

private data class AnotherTestValidationRuleDto(
    override val message: String,
) : ValidationRuleDto

private data class UnknownValidationRuleDto(
    override val message: String,
) : ValidationRuleDto

private data class TestValidationRule(
    override val message: String,
) : ValidationRule {
  override fun validate(value: String): ValidationResult = ValidationResult.Success
}

private data class AnotherTestValidationRule(
    override val message: String,
) : ValidationRule {
  override fun validate(value: String): ValidationResult = ValidationResult.Success
}

class CompositeValidationRuleMapperTest : BehaviorSpec() {
  init {
    Given("a CompositeValidationRuleMapper") {
      val mappers =
          mapOf(
              TestValidationRuleDto::class to
                  ValidationRuleMapper { dto ->
                    val testDto = dto as TestValidationRuleDto
                    TestValidationRule(testDto.message)
                  },
              AnotherTestValidationRuleDto::class to
                  ValidationRuleMapper { dto ->
                    val anotherDto = dto as AnotherTestValidationRuleDto
                    AnotherTestValidationRule(anotherDto.message)
                  },
          )
      val mapper = CompositeValidationRuleMapper(mappers)

      and("a known validation rule DTO") {
        val dto = TestValidationRuleDto("This is a test")

        When("the dto is mapped") {
          val rule = mapper.map(dto)

          Then("it should be mapped correctly") {
            rule.shouldBeInstanceOf<TestValidationRule>()
            rule.message shouldBe "This is a test"
          }
        }
      }

      and("an unknown validation rule DTO") {
        val dto = UnknownValidationRuleDto("I am unknown")

        When("the dto is mapped") {
          Then("it should throw an IllegalArgumentException") {
            val exception = shouldThrow<IllegalArgumentException> { mapper.map(dto) }
            exception.message shouldContain
                "Unsupported ValidationRuleDto type: UnknownValidationRuleDto"
          }
        }
      }
    }
  }
}
