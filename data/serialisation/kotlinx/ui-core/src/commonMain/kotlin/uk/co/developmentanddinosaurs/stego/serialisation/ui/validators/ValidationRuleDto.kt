package uk.co.developmentanddinosaurs.stego.serialisation.ui.validators

import kotlinx.serialization.Serializable

/**
 * DTO for declarative validation rules from the server.
 * The `type` field in the JSON will determine which subclass is used for deserialization.
 */
@Serializable
sealed interface ValidationRuleDto {
    val message: String
}
