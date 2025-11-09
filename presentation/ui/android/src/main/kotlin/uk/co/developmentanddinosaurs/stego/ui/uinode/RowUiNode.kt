package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.co.developmentanddinosaurs.stego.ui.node.RowUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode

@Composable
fun RenderRowUiNode(
    rowUiNode: RowUiNode,
    renderChild: @Composable (UiNode) -> Unit,
) {
  Row {
    rowUiNode.children.forEach { weightedChild ->
      Row(modifier = Modifier.weight(weightedChild.weight)) { renderChild(weightedChild.child) }
    }
  }
}
