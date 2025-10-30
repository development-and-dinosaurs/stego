package uk.co.developmentanddinosaurs.stego.ui.validators

data class RequiredValidationRule(
    override val message: String
) : ValidationRule {
    override fun validate(value: String): ValidationResult =
        if (value.isEmpty()) ValidationResult.Failure(message) else ValidationResult.Success
}
