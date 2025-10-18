package uk.co.developmentanddinosaurs.stego.app

import kotlinx.coroutines.delay
import uk.co.developmentanddinosaurs.stego.statemachine.*
import uk.co.developmentanddinosaurs.stego.ui.*

/**
 * A mock invokable that simulates a login network request.
 */
object LoginInvokable : Invokable {
    override suspend fun invoke(input: Map<String, DataValue>): InvokableResult {
        delay(2000)
        val username = (input["username"] as? StringPrimitive)?.value
        return if (username == "stego") {
            InvokableResult.Success(mapOf("loggedIn" to BooleanPrimitive(true)))
        } else {
            InvokableResult.Failure(mapOf("error" to StringPrimitive("Invalid username")))
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

val loginStateMachineDefinition = StateMachineDefinition(
    initial = "Idle",
    states = mapOf(
        "Idle" to UiState(
            id = "Idle",
            uiNode = ColumnView(
                children = listOf(
                    TextFieldView(
                        text = $$"${username}",
                        label = "Username",
                        onTextChanged = Event("TEXT_CHANGED")
                    ),
                    ButtonView(
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
            uiNode = ColumnView(
                children = listOf(
                    ProgressIndicatorView,
                    LabelView("Logging in...")
                )
            ),
            invoke = InvokableDefinition(
                "login",
                LoginInvokable,
                mapOf("username" to ContextReference("username"))
            ),
            on = mapOf(
                "done.invoke.login" to listOf(Transition("Success")),
                "error.invoke.login" to listOf(Transition("Error", actions = listOf(SaveErrorAction)))
            )
        ),
        "Success" to UiState(
            id = "Success",
            uiNode = ColumnView(
                children = listOf(
                    LabelView($$"Welcome, ${username}!")
                )
            )
        ),
        "Error" to UiState(
            id = "Error",
            uiNode = ColumnView(
                children = listOf(
                    LabelView($$"Error for ${username}: ${error}"),
                    ButtonView(
                        text = "Retry",
                        onClick = Event("RETRY")
                    )
                )
            ),
            on = mapOf("RETRY" to listOf(Transition("Idle")))
        )
    )
)
