package uk.co.developmentanddinosaurs.stego.ui.node

/**
 * Represents the state of a single field within a form.
 *
 * @property isValid Whether the field is currently valid.
 * @property triggerValidation A function that can be called to force the field to re-validate its current value.
 */
data class FieldState(
    val isValid: Boolean = false,
    val triggerValidation: () -> Boolean = { true },
)
