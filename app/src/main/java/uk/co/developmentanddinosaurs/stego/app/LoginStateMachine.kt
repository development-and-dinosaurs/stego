package uk.co.developmentanddinosaurs.stego.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.Invokable
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineDefinition
import uk.co.developmentanddinosaurs.stego.statemachine.StringPrimitive
import uk.co.developmentanddinosaurs.stego.statemachine.Transition
import uk.co.developmentanddinosaurs.stego.ui.ButtonView
import uk.co.developmentanddinosaurs.stego.ui.ColumnView
import uk.co.developmentanddinosaurs.stego.ui.LabelView
import uk.co.developmentanddinosaurs.stego.ui.ProgressIndicatorView
import uk.co.developmentanddinosaurs.stego.ui.TextFieldView
import uk.co.developmentanddinosaurs.stego.ui.UiState

/**
 * A mock invokable that simulates a login network request.
 */
object LoginInvokable : Invokable {
    override fun invoke(context: Context, scope: CoroutineScope): Deferred<Event> {
        return scope.async {
            delay(2000)
            val username = (context.get("username") as? StringPrimitive)?.value
            if (username == "stego") {
                Event("LOGIN_SUCCESS")
            } else {
                Event("LOGIN_FAIL", mapOf("error" to StringPrimitive("Invalid username")))
            }
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
            view = ColumnView(
                children = listOf(
                    TextFieldView(
                        text = "\${username}",
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
            view = ColumnView(
                children = listOf(
                    ProgressIndicatorView,
                    LabelView("Logging in...")
                )
            ),
            invoke = LoginInvokable,
            on = mapOf(
                "LOGIN_SUCCESS" to listOf(Transition("Success")),
                "LOGIN_FAIL" to listOf(Transition("Error", actions = listOf(SaveErrorAction)))
            )
        ),
        "Success" to UiState(
            id = "Success",
            view = ColumnView(
                children = listOf(
                    LabelView("Welcome, \${username}!")
                )
            )
        ),
        "Error" to UiState(
            id = "Error",
            view = ColumnView(
                children = listOf(
                    LabelView("Error for \${username}: \${error}"),
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
