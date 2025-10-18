package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull
import uk.co.developmentanddinosaurs.stego.statemachine.BooleanPrimitive
import uk.co.developmentanddinosaurs.stego.statemachine.ContextReference
import uk.co.developmentanddinosaurs.stego.statemachine.DataValue
import uk.co.developmentanddinosaurs.stego.statemachine.DoublePrimitive
import uk.co.developmentanddinosaurs.stego.statemachine.EventReference
import uk.co.developmentanddinosaurs.stego.statemachine.Invokable
import uk.co.developmentanddinosaurs.stego.statemachine.InvokableDefinition
import uk.co.developmentanddinosaurs.stego.statemachine.LongPrimitive
import uk.co.developmentanddinosaurs.stego.statemachine.StringPrimitive
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

        val inputMap: Map<String, DataValue> = dto.input?.mapValues { (_, value) ->
            jsonElementToDataValue(value)
        } ?: emptyMap()

        return InvokableDefinition(
            id = dto.id,
            src = invokable,
            input = inputMap,
        )
    }

    private fun jsonElementToDataValue(element: JsonElement): DataValue {
        if (element is JsonNull) throw StateMachineException("Null values are not supported in invokable input.")
        if (element !is JsonPrimitive) throw StateMachineException("Complex objects in invokable input are not supported. Use expressions to reference context data.")

        return when {
            element.isString -> {
                when {
                    element.content.startsWith("context.") -> ContextReference(element.content.substringAfter("context."))
                    element.content.startsWith("event.") -> EventReference(element.content.substringAfter("event."))
                    else -> StringPrimitive(element.content)
                }
            }
            element.longOrNull != null -> LongPrimitive(element.longOrNull!!)
            element.doubleOrNull != null -> DoublePrimitive(element.doubleOrNull!!)
            element.booleanOrNull != null -> BooleanPrimitive(element.booleanOrNull!!)
            else -> StringPrimitive(element.content) // Fallback for other primitive types
        }
    }
}