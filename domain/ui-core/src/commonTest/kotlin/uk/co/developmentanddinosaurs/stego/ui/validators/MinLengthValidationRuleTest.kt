package uk.co.developmentanddinosaurs.stego.ui.validators

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class MinLengthValidationRuleTest : BehaviorSpec() {
  init {
    Given("a MinLengthValidationRule with a min length of 3") {
      val rule = MinLengthValidationRule("Too short", 3)

      When("validating a string shorter than the min length") {
        val result = rule.validate("ab")

        Then("it should return Failure with the correct message") {
          result.shouldBeInstanceOf<ValidationResult.Failure>()
          result.message shouldBe "Too short"
        }
      }

      When("validating a string equal to the min length") {
        val result = rule.validate("abc")

        Then("it should return Success") { result.shouldBeInstanceOf<ValidationResult.Success>() }
      }

      When("validating a string longer than the min length") {
        val result = rule.validate("abcd")

        Then("it should return Success") { result.shouldBeInstanceOf<ValidationResult.Success>() }
      }
    }
  }
}
