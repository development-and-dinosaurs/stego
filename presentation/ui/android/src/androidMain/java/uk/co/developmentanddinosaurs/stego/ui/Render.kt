package uk.co.developmentanddinosaurs.stego.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.platform.LocalFocusManager
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.BypassValidationButtonAction
import uk.co.developmentanddinosaurs.stego.ui.node.ColumnUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.FieldState
import uk.co.developmentanddinosaurs.stego.ui.node.GridUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.ImageUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.InteractionDataKeys
import uk.co.developmentanddinosaurs.stego.ui.node.LabelUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.ProgressIndicatorUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.SubmitButtonAction
import uk.co.developmentanddinosaurs.stego.ui.node.TextFieldUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UserInteractionHandler
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderButtonUiNode
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderColumnUiNode
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderGridUiNode
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderImageUiNode
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderLabelUiNode
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderProgressIndicatorUiNode
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderTextFieldUiNode

@Composable
fun Render(
    uiNode: UiNode,
    context: Context,
    onEvent: (Event) -> Unit,
) {
    val formFields = remember { mutableStateMapOf<String, FieldState>() }
    var shakeTrigger by remember { mutableIntStateOf(0) }

    RenderInternal(uiNode, context, onEvent, formFields, shakeTrigger) {
        shakeTrigger++
    }
}

@Composable
private fun RenderInternal(
    uiNode: UiNode,
    context: Context,
    onEvent: (Event) -> Unit,
    formFields: SnapshotStateMap<String, FieldState>,
    shakeTrigger: Int,
    onShake: () -> Unit,
) {
    val onStateChange = { id: String, state: FieldState -> formFields[id] = state }
    val focusManager = LocalFocusManager.current

    val interactionHandler: UserInteractionHandler = { interactionId, data ->
        onEvent(Event(interactionId, data))
    }

    fun handleSubmit(buttonNode: ButtonUiNode) {
        val action = buttonNode.onClick as? SubmitButtonAction ?: return

        // Determine which fields to validate based on the scope from the JSON
        val fieldsToValidate =
            action.validationScope?.let { ids ->
                formFields.filterKeys { it in ids }
            } ?: formFields // If scope is null, validate all fields

        // 1. Trigger validation for the relevant fields and collect the synchronous results
        val validationResults = fieldsToValidate.values.map { it.triggerValidation() }

        // 2. Check if they are all valid
        val isFormValid = validationResults.all { it }

        // 3. Act on the result
        if (isFormValid) {
            focusManager.clearFocus() // Commit data from focused fields
            interactionHandler(action.trigger, mapOf(InteractionDataKeys.COMPONENT_ID to buttonNode.id))
        } else if (action.onValidationFail == "shake") {
            onShake()
        }
    }

    fun handleBypass(buttonNode: ButtonUiNode) {
        val action = buttonNode.onClick as? BypassValidationButtonAction ?: return
        focusManager.clearFocus()
        interactionHandler(action.trigger, mapOf(InteractionDataKeys.COMPONENT_ID to buttonNode.id))
    }

    // The when block is now a clean dispatcher.
    when (uiNode) {
        is ButtonUiNode -> RenderButton(uiNode, context, ::handleSubmit, ::handleBypass)
        is ColumnUiNode -> RenderColumn(uiNode, context, onEvent, formFields, shakeTrigger, onShake)
        is GridUiNode -> RenderGrid(uiNode, context, onEvent, formFields, shakeTrigger, onShake)
        is ImageUiNode -> RenderImageUiNode(uiNode)
        is LabelUiNode -> RenderLabel(uiNode, context)
        is ProgressIndicatorUiNode -> RenderProgressIndicatorUiNode()
        is TextFieldUiNode -> RenderTextField(uiNode, context, interactionHandler, onStateChange, shakeTrigger)
    }
}

// Each of these private functions encapsulates the logic for a single node type.

@Composable
private fun RenderGrid(
    uiNode: GridUiNode,
    context: Context,
    onEvent: (Event) -> Unit,
    formFields: SnapshotStateMap<String, FieldState>,
    shakeTrigger: Int,
    onShake: () -> Unit,
) {
    RenderGridUiNode(uiNode) { childNode ->
        RenderInternal(childNode, context, onEvent, formFields, shakeTrigger, onShake)
    }
}

@Composable
private fun RenderColumn(
    uiNode: ColumnUiNode,
    context: Context,
    onEvent: (Event) -> Unit,
    formFields: SnapshotStateMap<String, FieldState>,
    shakeTrigger: Int,
    onShake: () -> Unit,
) {
    RenderColumnUiNode(uiNode) { childNode ->
        RenderInternal(childNode, context, onEvent, formFields, shakeTrigger, onShake)
    }
}

@Composable
private fun RenderLabel(
    uiNode: LabelUiNode,
    context: Context,
) {
    val resolvedNode = uiNode.copy(text = resolve(uiNode.text, context))
    RenderLabelUiNode(resolvedNode)
}

@Composable
private fun RenderButton(
    uiNode: ButtonUiNode,
    context: Context,
    handleSubmit: (ButtonUiNode) -> Unit,
    handleBypass: (ButtonUiNode) -> Unit,
) {
    val resolvedNode = uiNode.copy(text = resolve(uiNode.text, context))
    RenderButtonUiNode(
        buttonUiNode = resolvedNode,
        onClick = {
            when (uiNode.onClick) {
                is SubmitButtonAction -> handleSubmit(uiNode)
                is BypassValidationButtonAction -> handleBypass(uiNode)
            }
        },
    )
}

@Composable
private fun RenderTextField(
    uiNode: TextFieldUiNode,
    context: Context,
    interactionHandler: UserInteractionHandler,
    onStateChange: (id: String, state: FieldState) -> Unit,
    shakeTrigger: Int,
) {
    val resolvedNode =
        uiNode.copy(
            text = resolve(uiNode.text, context),
            label = resolve(uiNode.label, context),
        )
    RenderTextFieldUiNode(resolvedNode, interactionHandler, onStateChange, shakeTrigger)
}
