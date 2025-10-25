package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonUiNode

@Composable
fun RenderButtonUiNode(
    buttonUiNode: ButtonUiNode,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
    ) {
        Text(text = buttonUiNode.text)
    }
}
