package uk.co.developmentanddinosaurs.stego.serialisation.ui.validators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("minLength")
data class MinLengthValidationRuleDto(
    override val message: String,
    val length: Int
) : ValidationRuleDto