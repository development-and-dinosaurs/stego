package uk.co.developmentanddinosaurs.stego.serialisation

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.DataValueDto

/**
 * A serializable representation of an `InvokableDefinition` from a JSON definition.
 */
@Serializable
data class InvokableDefinitionDto(
    val id: String,
    val src: String,
    val input: Map<String, DataValueDto> = emptyMap(),
)
