package uk.co.developmentanddinosaurs.stego.ui

import androidx.compose.runtime.Composable
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.ui.node.*
import uk.co.developmentanddinosaurs.stego.ui.uinode.*

@Composable
fun Render(uiNode: UiNode, context: Context, onEvent: (Event) -> Unit) {
    val interactionHandler: UserInteractionHandler = {
        interactionId, data -> onEvent(Event(interactionId, data))
    }
    when (uiNode) {
        is ColumnUiNode -> {
            RenderColumnUiNode(uiNode, renderChild = { childNode: UiNode ->
                Render(childNode, context, onEvent)
            })
        }
        is LabelUiNode -> {
            val resolvedNode = uiNode.copy(text = resolve(uiNode.text, context))
            RenderLabelUiNode(resolvedNode)
        }
        is ButtonUiNode -> {
            val resolvedNode = uiNode.copy(text = resolve(uiNode.text, context))
            RenderButtonUiNode(resolvedNode, interactionHandler)
        }
        is TextFieldUiNode -> {
            val resolvedNode = uiNode.copy(
                text = resolve(uiNode.text, context),
                label = resolve(uiNode.label, context)
            )
            RenderTextFieldUiNode(resolvedNode, interactionHandler)
        }
        is ProgressIndicatorUiNode -> RenderProgressIndicatorUiNode()
    }
}
