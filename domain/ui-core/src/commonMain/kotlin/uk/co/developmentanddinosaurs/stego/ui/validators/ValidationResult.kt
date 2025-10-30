package uk.co.developmentanddinosaurs.stego.ui.validators

/**
 * Represents the outcome of a validation check.
 */
sealed interface ValidationResult {
    /**
     * Represents a successful validation.
     */
    object Success : ValidationResult

    /**
     * Represents a failed validation.
     * @property message The error message describing the failure.
     */
    data class Failure(val message: String) : ValidationResult
}
