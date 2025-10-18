package uk.co.developmentanddinosaurs.stego.ui

import androidx.compose.runtime.Composable
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.ui.node.*
import uk.co.developmentanddinosaurs.stego.ui.uinode.*

@Composable
fun Render(uiNode: UiNode, context: Context, onEvent: (Event) -> Unit) {
    when (uiNode) {
        is ColumnUiNode -> RenderColumnUiNode(uiNode, context, onEvent)
        is LabelUiNode -> RenderLabelUiNode(uiNode, context)
        is ButtonUiNode -> RenderButtonUiNode(uiNode, context, onEvent)
        is TextFieldUiNode -> RenderTextFieldUiNode(uiNode, context, onEvent)
        is ProgressIndicatorUiNode -> RenderProgressIndicatorUiNode()
    }
}
