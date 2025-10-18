package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.ui.resolve
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonUiNode

@Composable
fun RenderButtonUiNode(buttonUiNode: ButtonUiNode, context: Context, onEvent: (Event) -> Unit) {
    Button(onClick = { onEvent(buttonUiNode.onClick) }) {
        Text(text = resolve(buttonUiNode.text, context))
    }
}
