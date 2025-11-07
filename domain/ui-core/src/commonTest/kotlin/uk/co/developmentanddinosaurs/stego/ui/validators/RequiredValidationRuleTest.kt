package uk.co.developmentanddinosaurs.stego.ui.validators

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class RequiredValidationRuleTest :
    BehaviorSpec({
      Given("a RequiredValidationRule") {
        val rule = RequiredValidationRule("Field is required")

        When("validating an empty string") {
          val result = rule.validate("")

          Then("it should return Failure with the correct message") {
            result.shouldBeInstanceOf<ValidationResult.Failure>()
            result.message shouldBe "Field is required"
          }
        }

        When("validating a non-empty string") {
          val result = rule.validate("some value")

          Then("it should return Success") { result.shouldBeInstanceOf<ValidationResult.Success>() }
        }
      }
    })
