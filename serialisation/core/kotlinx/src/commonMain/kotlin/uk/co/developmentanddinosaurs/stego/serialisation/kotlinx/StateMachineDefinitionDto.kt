package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue.DataValueDto

@Serializable
data class StateMachineDefinitionDto(
    val initial: String,
    val states: Map<String, StateDto>,
    val initialContext: Map<String, DataValueDto> = emptyMap()
)
