package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import uk.co.developmentanddinosaurs.stego.ui.node.LabelUiNode

@Composable
fun RenderLabelUiNode(labelUiNode: LabelUiNode) {
    Text(text = labelUiNode.text)
}
