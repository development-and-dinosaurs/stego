package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineDefinition

@Serializable
data class StateMachineDefinitionDto(
    val initial: String,
    val states: Map<String, StateDto>,
    val initialContext: Map<String, DataValueDto> = emptyMap()
) {
    fun toDomain(): StateMachineDefinition {
        var context = Context()
        initialContext.forEach { (key, valueDto) ->
            val value = valueDto.toDomain() ?: return@forEach
            context = context.put(key, value)
        }
        return StateMachineDefinition(
            initial = initial,
            states = states.mapValues { (_, stateDto) -> stateDto.toDomain() },
            initialContext = context
        )
    }
}
