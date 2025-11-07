package uk.co.developmentanddinosaurs.stego.serialisation.ui.validators

/**
 * DTO for declarative validation rules from the server. The `type` field in the JSON will determine
 * which subclass is used for deserialization.
 */
interface ValidationRuleDto {
  val message: String
}
