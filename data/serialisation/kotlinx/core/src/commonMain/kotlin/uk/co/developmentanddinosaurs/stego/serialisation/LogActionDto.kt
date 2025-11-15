package uk.co.developmentanddinosaurs.stego.serialisation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The serializable Data Transfer Object (DTO) for a log action.
 *
 * This class represents an action that logs a message. It is typically deserialized
 * from a configuration file (e.g., JSON) and then mapped to a domain [uk.co.developmentanddinosaurs.stego.statemachine.LogAction]
 * by the [uk.co.developmentanddinosaurs.stego.serialisation.mappers.ActionMapper].
 *
 * @property message The static string message to be logged.
 */
@Serializable
@SerialName("log")
data class LogActionDto(
    val message: String,
) : ActionDto
