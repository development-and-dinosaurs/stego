package uk.co.developmentanddinosaurs.stego.app

import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.*
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.*
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.*
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

fun loadLoginStateMachineDefinitionJsonString(context:  android.content.Context): String {
    val inputStream = context.resources.openRawResource(R.raw.login_state_machine)
    val reader = InputStreamReader(inputStream)
    val jsonString = reader.readText()
    println(jsonString)
    reader.close()
    return jsonString
}

private val json = Json {
    serializersModule = SerializersModule {
        polymorphic(StateDto::class) {
            subclass(LogicStateDto::class)
            subclass(UiStateDto::class)
        }
        polymorphic(ActionDto::class){
            subclass(LogActionDto::class)
            subclass(AssignActionDto::class)
        }
        polymorphic(UiNodeDto::class) {
            subclass(ColumnUiNodeDto::class)
            subclass(TextFieldUiNodeDto::class)
            subclass(ButtonUiNodeDto::class)
            subclass(ProgressIndicatorUiNodeDto::class)
            subclass(LabelUiNodeDto::class)
        }
    }
}

fun stateDefDto(context: android.content.Context): StateMachineDefinitionDto = json.decodeFromString<StateMachineDefinitionDto>(loadLoginStateMachineDefinitionJsonString(context))

fun stateDef(context: android.content.Context): StateMachineDefinition {
    val invokableMapper = InvokableDefinitionMapper(mapOf("LoginInvokable" to LoginInvokable))
    val actionMapper = CompositeActionMapper(
        mapOf(
            AssignActionDto::class to AssignActionMapper(),
            LogActionDto::class to LogActionMapper { message -> println(message) }
        )
    )
    val interactionMapper = InteractionMapper()
    val uiNodeMapper = CompositeUiNodeMapper(
        simpleMappers = mapOf(
            LabelUiNodeDto::class to LabelUiNodeMapper(),
            ProgressIndicatorUiNodeDto::class to ProgressIndicatorUiNodeMapper(),
            TextFieldUiNodeDto::class to TextFieldUiNodeMapper(interactionMapper),
            ButtonUiNodeDto::class to ButtonUiNodeMapper(interactionMapper)
        ),
        compositeAwareFactories = mapOf(
            ColumnUiNodeDto::class to { mapper -> ColumnUiNodeMapper(mapper) }
        )
    )

    val transitionMapper = TransitionMapper(actionMapper)
    val compositeStateMapper = CompositeStateMapper(
        mapperFactories = mapOf(
            LogicStateDto::class to { stateMapper ->
                LogicStateMapper(stateMapper, invokableMapper, transitionMapper, actionMapper)
            },
            UiStateDto::class to { stateMapper ->
                UiStateMapper(stateMapper, actionMapper, invokableMapper, transitionMapper, uiNodeMapper)
            }
        )
    )

    // Perform the mapping using the fully constructed composite mapper.
    val stateMachineDefinition = compositeStateMapper.map(stateDefDto(context))
    println(stateMachineDefinition)
    return stateMachineDefinition
}
