package uk.co.developmentanddinosaurs.stego.ui.validators

/**
 * Domain models for declarative validation rules.
 */
sealed interface ValidationRule {
    val message: String
    fun validate(value: String): ValidationResult
}
