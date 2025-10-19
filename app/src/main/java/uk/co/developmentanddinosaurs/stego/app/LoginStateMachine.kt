package uk.co.developmentanddinosaurs.stego.app

import kotlinx.coroutines.delay
import uk.co.developmentanddinosaurs.stego.statemachine.*
import uk.co.developmentanddinosaurs.stego.statemachine.guards.EqualsGuard
import uk.co.developmentanddinosaurs.stego.ui.UiState
import uk.co.developmentanddinosaurs.stego.ui.node.*

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

/**
 * An action that saves the username from a TEXT_CHANGED event's data payload into the context.
 */
data object SaveUsernameAction : Action {
    override fun execute(context: Context, event: Event): Context {
        val username = event.data["text"] ?: return context
        return context.put("username", username)
    }
}

/**
 * An action that saves an error message from an event's data payload into the context.
 */
data object SaveErrorAction : Action {
    override fun execute(context: Context, event: Event): Context {
        val errorMessage = event.data["error"] ?: return context
        return context.put("error", errorMessage)
    }
}

//
//fun loadLoginStateMachineDefinitionJsonString(context:  android.content.Context): String {
//    val inputStream = context.resources.openRawResource(R.raw.login_state_machine)
//    val reader = InputStreamReader(inputStream)
//    val jsonString = reader.readText()
//    reader.close()
//    return jsonString
//}
//
//private val json = Json {
//    serializersModule = SerializersModule {
//        polymorphic(StateDto::class) {
//            subclass(LogicStateDto::class)
//            subclass(UiStateDto::class)
//        }
//        polymorphic(ActionDto::class){
//            subclass(LogActionDto::class)
//            subclass(AssignActionDto::class)
//        }
//        polymorphic(ValueReferenceDto::class){
//            subclass(EventReferenceDto::class)
//        }
//        polymorphic(UiNodeDto::class) {
//            subclass(ColumnUiNodeDto::class)
//            subclass(TextFieldUiNodeDto::class)
//            subclass(ButtonUiNodeDto::class)
//            subclass(ProgressIndicatorUiNodeDto::class)
//            subclass(LabelUiNodeDto::class)
//        }
//    }
//}
//
//fun stateDefDto(context: android.content.Context): StateMachineDefinitionDto = json.decodeFromString<StateMachineDefinitionDto>(loadLoginStateMachineDefinitionJsonString(context))
//
//fun stateDef(context: android.content.Context): StateMachineDefinition = stateDefDto(context).toDomain()

val loginStateMachineDefinition = StateMachineDefinition(
    initial = "Idle",
    states = mapOf(
        "Idle" to UiState(
            id = "Idle",
            uiNode = ColumnUiNode(
                children = listOf(
                    TextFieldUiNode(
                        text = $$"${username}",
                        label = "Username",
                        onTextChanged = Event("TEXT_CHANGED")
                    ),
                    ButtonUiNode(
                        text = "Log In",
                        onClick = Event("SUBMIT")
                    )
                )
            ),
            on = mapOf(
                "TEXT_CHANGED" to listOf(
                    Transition(target = "Idle", actions = listOf(SaveUsernameAction))
                ),
                "SUBMIT" to listOf(
                    Transition(target = "Loading")
                )
            )
        ),
        "Loading" to UiState(
            id = "Loading",
            uiNode = ColumnUiNode(
                children = listOf(
                    ProgressIndicatorUiNode,
                    LabelUiNode("Logging in...")
                )
            ),
            invoke = InvokableDefinition(
                "login",
                LoginInvokable,
                mapOf("username" to "{context.username}")
            ),
            on = mapOf(
                "done.invoke.login" to listOf(
                    Transition("Success", guard = EqualsGuard("{event.loggedIn}", true))
                ),
                "error.invoke.login" to listOf(Transition("Error", actions = listOf(SaveErrorAction)))
            )
        ),
        "Success" to UiState(
            id = "Success",
            uiNode = ColumnUiNode(
                children = listOf(
                    LabelUiNode($$"Welcome, ${username}!")
                )
            )
        ),
        "Error" to UiState(
            id = "Error",
            uiNode = ColumnUiNode(
                children = listOf(
                    LabelUiNode($$"Error for ${username}: ${error}"),
                    ButtonUiNode(
                        text = "Retry",
                        onClick = Event("RETRY")
                    )
                )
            ),
            on = mapOf("RETRY" to listOf(Transition("Idle")))
        )
    )
)
