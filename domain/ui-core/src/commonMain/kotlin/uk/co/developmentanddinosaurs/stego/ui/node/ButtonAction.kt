package uk.co.developmentanddinosaurs.stego.ui.node

/**
 * Domain model for a button's action, including its validation strategy.
 */
sealed interface ButtonAction {
    val trigger: String
}

data class SubmitButtonAction(
    override val trigger: String,
    val validationScope: List<String>? = null,
    val onValidationFail: String = "shake"
) : ButtonAction

data class BypassValidationButtonAction(
    override val trigger: String
) : ButtonAction