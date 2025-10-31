package uk.co.developmentanddinosaurs.stego.serialisation.ui.validators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("required")
data class RequiredValidationRuleDto(
    override val message: String,
) : ValidationRuleDto
