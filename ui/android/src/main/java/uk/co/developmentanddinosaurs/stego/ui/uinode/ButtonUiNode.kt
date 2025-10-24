package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import uk.co.developmentanddinosaurs.stego.ui.node.InteractionDataKeys
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UserInteractionHandler

@Composable
fun RenderButtonUiNode(buttonUiNode: ButtonUiNode, userInteractionHandler: UserInteractionHandler) {
    Button(onClick = {
        userInteractionHandler(
            buttonUiNode.onClick.trigger,
            mapOf(InteractionDataKeys.COMPONENT_ID to buttonUiNode.id)
        )
    }) {
        Text(text = buttonUiNode.text)
    }
}
