package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MaxLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto
import uk.co.developmentanddinosaurs.stego.ui.validators.MaxLengthValidationRule
import uk.co.developmentanddinosaurs.stego.ui.validators.MinLengthValidationRule
import uk.co.developmentanddinosaurs.stego.ui.validators.RequiredValidationRule
import uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule

/**
 * Maps validation rule DTOs to their corresponding domain models.
 */
class ValidationRuleMapper {
    fun map(dto: ValidationRuleDto): ValidationRule = when (dto) {
        is MinLengthValidationRuleDto -> MinLengthValidationRule(dto.message, dto.length)
        is MaxLengthValidationRuleDto -> MaxLengthValidationRule(dto.message, dto.length)
        is RequiredValidationRuleDto -> RequiredValidationRule(dto.message)
    }

    fun map(dtos: List<ValidationRuleDto>): List<ValidationRule> = dtos.map(::map)
}
