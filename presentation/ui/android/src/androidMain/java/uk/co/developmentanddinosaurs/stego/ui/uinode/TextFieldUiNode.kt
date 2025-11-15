package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import uk.co.developmentanddinosaurs.stego.ui.node.FieldState
import uk.co.developmentanddinosaurs.stego.ui.node.InteractionDataKeys
import uk.co.developmentanddinosaurs.stego.ui.node.TextFieldUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UserInteractionHandler
import uk.co.developmentanddinosaurs.stego.ui.shake
import uk.co.developmentanddinosaurs.stego.ui.validators.ValidationResult

@Composable
fun RenderTextFieldUiNode(
    textFieldUiNode: TextFieldUiNode,
    userInteractionHandler: UserInteractionHandler,
    onStateChange: (id: String, state: FieldState) -> Unit,
    shakeTrigger: Int,
) {
    val focusManager = LocalFocusManager.current
    var text by remember { mutableStateOf(textFieldUiNode.text) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasBeenFocused by remember { mutableStateOf(false) }

    fun validateAndReport(
        value: String,
        isFocused: Boolean = hasBeenFocused,
    ) {
        val firstError =
            textFieldUiNode.validation
                .asSequence()
                .map { it.validate(value) }
                .filterIsInstance<ValidationResult.Failure>()
                .firstOrNull()
        if (isFocused) {
            errorMessage = firstError?.message
        }
        onStateChange(
            textFieldUiNode.id,
            FieldState(
                isValid = firstError == null,
                triggerValidation = {
                    validateAndReport(text, true)
                    return@FieldState textFieldUiNode.validation.all { it.validate(text) is ValidationResult.Success }
                },
            ),
        )
    }

    // Report initial state
    LaunchedEffect(Unit) {
        validateAndReport(text)
    }

    LaunchedEffect(textFieldUiNode.text) {
        if (text != textFieldUiNode.text) {
            text = textFieldUiNode.text
            validateAndReport(text)
        }
    }

    fun commitChange() {
        val hasFailed = textFieldUiNode.validation.any { it.validate(text) is ValidationResult.Failure }
        if (!hasFailed) {
            userInteractionHandler(
                textFieldUiNode.onTextChanged.trigger,
                mapOf(
                    InteractionDataKeys.COMPONENT_ID to textFieldUiNode.id,
                    InteractionDataKeys.COMPONENT_TEXT to text,
                ),
            )
        }
    }

    TextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            if (hasBeenFocused) {
                validateAndReport(newText, true)
            }
        },
        label = { Text(textFieldUiNode.label) },
        supportingText = {
            errorMessage?.let { Text(it) }
        },
        isError = errorMessage != null,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions =
            KeyboardActions(onDone = {
                commitChange()
                focusManager.clearFocus()
            }),
        modifier =
            Modifier
                .shake(if (errorMessage != null) shakeTrigger else 0)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) hasBeenFocused = true
                    if (!focusState.isFocused) {
                        validateAndReport(text, true)
                        commitChange()
                    }
                },
    )
}
