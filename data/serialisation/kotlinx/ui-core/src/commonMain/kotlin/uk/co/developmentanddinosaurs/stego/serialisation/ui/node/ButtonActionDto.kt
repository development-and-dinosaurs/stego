package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for a button's action, including its validation strategy.
 */
@Serializable
sealed interface ButtonActionDto {
    val trigger: String
}

@Serializable
@SerialName("submit")
data class SubmitButtonActionDto(
    override val trigger: String,
    val validationScope: List<String>? = null,
    val onValidationFail: String = "shake",
) : ButtonActionDto

@Serializable
@SerialName("bypassValidation")
data class BypassValidationButtonActionDto(
    override val trigger: String,
) : ButtonActionDto
