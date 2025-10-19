package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Invokable
import uk.co.developmentanddinosaurs.stego.statemachine.InvokableDefinition
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException

/**
 * Maps an [InvokableDefinitionDto] to an [InvokableDefinition] domain object.
 *
 * @param invokableRegistry A map where keys are string identifiers and values are the corresponding [Invokable] implementations.
 */
class InvokableDefinitionMapper(private val invokableRegistry: Map<String, Invokable>) {
    /**
     * Performs the mapping from DTO to domain object.
     */
    fun map(dto: InvokableDefinitionDto): InvokableDefinition {
        val invokable = invokableRegistry[dto.src]
            ?: throw StateMachineException("Invokable source '${dto.src}' not found in registry.")

        val inputMap = dto.input.mapValues { (_, valueDto) -> valueDto.toDomain() }

        return InvokableDefinition(
            id = dto.id,
            src = invokable,
            input = inputMap,
        )
    }
}
