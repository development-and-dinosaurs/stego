package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MaxLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.ui.validators.MaxLengthValidationRule
import uk.co.developmentanddinosaurs.stego.ui.validators.MinLengthValidationRule
import uk.co.developmentanddinosaurs.stego.ui.validators.RequiredValidationRule

class ValidationRuleMapperTest :
    BehaviorSpec({
        Given("a ValidationRuleMapper") {
            val mapper = ValidationRuleMapper()

            When("mapping a MinLengthValidationRuleDto") {
                val dto = MinLengthValidationRuleDto("Too short", 5)
                val rule = mapper.map(dto)

                Then("it should return a MinLengthValidationRule with correct properties") {
                    rule.shouldBeInstanceOf<MinLengthValidationRule>()
                    rule.message shouldBe "Too short"
                    rule.length shouldBe 5
                }
            }

            When("mapping a MaxLengthValidationRuleDto") {
                val dto = MaxLengthValidationRuleDto("Too long", 10)
                val rule = mapper.map(dto)

                Then("it should return a MaxLengthValidationRule with correct properties") {
                    rule.shouldBeInstanceOf<MaxLengthValidationRule>()
                    rule.message shouldBe "Too long"
                    rule.length shouldBe 10
                }
            }

            When("mapping a RequiredValidationRuleDto") {
                val dto = RequiredValidationRuleDto("Is required")
                val rule = mapper.map(dto)

                Then("it should return a RequiredValidationRule with correct properties") {
                    rule.shouldBeInstanceOf<RequiredValidationRule>()
                    rule.message shouldBe "Is required"
                }
            }

            When("mapping a list of validation rule DTOs") {
                val dtos =
                    listOf(
                        MinLengthValidationRuleDto("min", 1),
                        MaxLengthValidationRuleDto("max", 99),
                    )
                val rules = mapper.map(dtos)

                Then("it should return a list of mapped validation rules") {
                    rules shouldHaveSize 2
                    rules[0].shouldBeInstanceOf<MinLengthValidationRule>()
                    rules[1].shouldBeInstanceOf<MaxLengthValidationRule>()
                }
            }

            When("mapping an empty list of validation rule DTOs") {
                val rules = mapper.map(emptyList())

                Then("it should return an empty list") {
                    rules.shouldBeEmpty()
                }
            }
        }
    })
