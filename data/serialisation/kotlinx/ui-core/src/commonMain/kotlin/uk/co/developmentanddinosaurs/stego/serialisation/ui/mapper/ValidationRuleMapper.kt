package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto
import uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule

/**
 * Defines a contract for mapping a [ValidationRuleDto] to a domain [ValidationRule].
 */
fun interface ValidationRuleMapper {
    fun map(dto: ValidationRuleDto): ValidationRule
}
