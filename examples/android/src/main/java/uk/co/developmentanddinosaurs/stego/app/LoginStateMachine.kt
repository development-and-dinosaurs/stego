package uk.co.developmentanddinosaurs.stego.app

import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import uk.co.developmentanddinosaurs.stego.serialisation.StateMachineDefinitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.CompositeStateMapper
import uk.co.developmentanddinosaurs.stego.serialisation.module.stegoCoreSerializersModule
import uk.co.developmentanddinosaurs.stego.serialisation.ui.module.stegoUiSerializersModule
import uk.co.developmentanddinosaurs.stego.statemachine.Invokable
import uk.co.developmentanddinosaurs.stego.statemachine.InvokableResult
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineDefinition
import java.io.InputStreamReader

/**
 * A mock invokable that simulates a login network request.
 */
object LoginInvokable : Invokable {
    override suspend fun invoke(input: Map<String, Any?>): InvokableResult {
        delay(2000)
        val username = input["username"]
        println("username is $username")
        return if (username == "stego") {
            InvokableResult.Success(mapOf("loggedIn" to true))
        } else {
            InvokableResult.Failure(mapOf("error" to "Invalid username"))
        }
    }
}

fun loadLoginStateMachineDefinitionJsonString(context: android.content.Context): String {
    val inputStream = context.resources.openRawResource(R.raw.login_state_machine)
    val reader = InputStreamReader(inputStream)
    val jsonString = reader.readText()
    reader.close()
    return jsonString
}

private val json = Json {
    serializersModule = stegoCoreSerializersModule + stegoUiSerializersModule
}

fun stateDefDto(context: android.content.Context): StateMachineDefinitionDto =
    json.decodeFromString(loadLoginStateMachineDefinitionJsonString(context))

fun stateDef(
    context: android.content.Context,
    compositeStateMapper: CompositeStateMapper
): StateMachineDefinition {
    return compositeStateMapper.map(stateDefDto(context))
}
