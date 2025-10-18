package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull
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

        val inputMap = dto.input?.mapValues { (_, value) ->
            jsonElementToAny(value)
        } ?: emptyMap()

        return InvokableDefinition(
            id = dto.id,
            src = invokable,
            input = inputMap,
        )
    }

    private fun jsonElementToAny(element: JsonElement): Any {
        if (element is JsonNull) throw StateMachineException("Null values are not supported in invokable input.")
        if (element !is JsonPrimitive) throw StateMachineException("Complex objects in invokable input are not supported. Use expressions to reference context data.")

        return when {
            element.isString -> element.content
            element.longOrNull != null -> element.longOrNull!!
            element.doubleOrNull != null -> element.doubleOrNull!!
            element.booleanOrNull != null -> element.booleanOrNull!!
            else -> element.content // Fallback for other primitive types
        }
    }
}