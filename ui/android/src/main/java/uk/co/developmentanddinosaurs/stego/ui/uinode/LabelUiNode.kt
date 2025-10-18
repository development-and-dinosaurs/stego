package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.ui.node.LabelUiNode
import uk.co.developmentanddinosaurs.stego.ui.resolve

@Composable
fun RenderLabelUiNode(labelUiNode: LabelUiNode, context: Context) {
    Text(text = resolve(labelUiNode.text, context))
}
