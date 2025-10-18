package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.StringPrimitive
import uk.co.developmentanddinosaurs.stego.ui.node.TextFieldUiNode
import uk.co.developmentanddinosaurs.stego.ui.resolve

@Composable
fun RenderTextFieldUiNode(textFieldUiNode: TextFieldUiNode, context: Context, onEvent: (Event) -> Unit) {
    val resolvedText = resolve(textFieldUiNode.text, context)
    var text by remember { mutableStateOf(resolvedText) }

    LaunchedEffect(resolvedText) {
        if (text != resolvedText) {
            text = resolvedText
        }
    }

    TextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            val eventData = textFieldUiNode.onTextChanged.data.toMutableMap()
            eventData["text"] = StringPrimitive(newText)
            onEvent(textFieldUiNode.onTextChanged.copy(data = eventData))
        },
        label = { Text(resolve(textFieldUiNode.label, context)) }
    )
}
