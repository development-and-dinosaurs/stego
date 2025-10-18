package uk.co.developmentanddinosaurs.stego.ui

import androidx.compose.runtime.Composable
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderButtonUiNode
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderColumnUiNode
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderLabelUiNode
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderProgressIndicatorUiNode
import uk.co.developmentanddinosaurs.stego.ui.uinode.RenderTextFieldUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.ColumnUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.LabelUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.ProgressIndicatorUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.TextFieldUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode

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
