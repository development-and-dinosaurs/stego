package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import uk.co.developmentanddinosaurs.stego.ui.node.ColumnUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode

@Composable
fun RenderColumnUiNode(columnUiNode: ColumnUiNode, renderChild: @Composable (UiNode) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        columnUiNode.children.forEach {
            renderChild(it)
        }
    }
}
