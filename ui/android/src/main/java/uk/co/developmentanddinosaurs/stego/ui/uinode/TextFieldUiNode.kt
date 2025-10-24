package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import uk.co.developmentanddinosaurs.stego.ui.node.TextFieldUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.InteractionDataKeys
import uk.co.developmentanddinosaurs.stego.ui.node.UserInteractionHandler

@Composable
fun RenderTextFieldUiNode(
    textFieldUiNode: TextFieldUiNode,
    userInteractionHandler: UserInteractionHandler
) {
    var text by remember { mutableStateOf(textFieldUiNode.text) }

    LaunchedEffect(textFieldUiNode.text) {
        if (text != textFieldUiNode.text) {
            text = textFieldUiNode.text
        }
    }

    TextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            userInteractionHandler(textFieldUiNode.onTextChanged.trigger, mapOf(
                InteractionDataKeys.COMPONENT_ID to textFieldUiNode.id,
                InteractionDataKeys.COMPONENT_TEXT to newText
            ))
        },
        label = { Text(textFieldUiNode.label) }
    )
}
