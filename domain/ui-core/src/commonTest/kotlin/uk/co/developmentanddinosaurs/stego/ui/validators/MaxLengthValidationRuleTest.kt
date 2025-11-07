package uk.co.developmentanddinosaurs.stego.ui.validators

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class MaxLengthValidationRuleTest : BehaviorSpec() {
  init {
    Given("a MaxLengthValidationRule with a max length of 5") {
      val rule = MaxLengthValidationRule("Too long", 5)

      When("validating a string shorter than the max length") {
        val result = rule.validate("abc")

        Then("it should return Success") { result.shouldBeInstanceOf<ValidationResult.Success>() }
      }

      When("validating a string equal to the max length") {
        val result = rule.validate("abcde")

        Then("it should return Success") { result.shouldBeInstanceOf<ValidationResult.Success>() }
      }

      When("validating a string longer than the max length") {
        val result = rule.validate("abcdef")

        Then("it should return Failure with the correct message") {
          result.shouldBeInstanceOf<ValidationResult.Failure>()
          result.message shouldBe "Too long"
        }
      }
    }
  }
}
