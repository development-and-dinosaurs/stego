package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue.DataValueDto

/**
 * The serializable Data Transfer Object (DTO) for an assign action.
 *
 * This class represents an action that assigns a value to a key in the state machine's context.
 * It is typically deserialized from a configuration file (e.g., JSON) and then mapped
 * to a domain [uk.co.developmentanddinosaurs.stego.statemachine.AssignAction] by the [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers.ActionMapper].
 *
 * @property key The name of the variable in the context to assign the value to.
 * @property value The value to be assigned, represented as a [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue.DataValueDto].
 */
@Serializable
@SerialName("assign")
data class AssignActionDto(val key: String, val value: DataValueDto) : ActionDto
