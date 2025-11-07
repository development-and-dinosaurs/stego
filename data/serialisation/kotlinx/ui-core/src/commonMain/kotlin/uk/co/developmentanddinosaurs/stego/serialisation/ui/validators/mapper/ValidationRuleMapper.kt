package uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto
import uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule

/**
 * Defines a contract for mapping a
 * [uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto] to a domain
 * [uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule].
 */
fun interface ValidationRuleMapper {
  fun map(dto: ValidationRuleDto): ValidationRule
}
