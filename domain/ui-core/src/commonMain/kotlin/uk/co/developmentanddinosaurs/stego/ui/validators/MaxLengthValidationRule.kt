package uk.co.developmentanddinosaurs.stego.ui.validators

import uk.co.developmentanddinosaurs.stego.annotations.StegoComponent

@StegoComponent("maxLength")
data class MaxLengthValidationRule(
    override val message: String,
    val length: Int,
) : ValidationRule {
    override fun validate(value: String): ValidationResult =
        if (value.length > length) {
            ValidationResult.Failure(message)
        } else {
            ValidationResult.Success
        }
}
