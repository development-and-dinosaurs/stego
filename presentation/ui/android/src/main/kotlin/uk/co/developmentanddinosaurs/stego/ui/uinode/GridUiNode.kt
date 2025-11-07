package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import uk.co.developmentanddinosaurs.stego.ui.node.GridUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode

@Composable
fun RenderGridUiNode(
    gridUiNode: GridUiNode,
    renderChild: @Composable (UiNode) -> Unit,
) {
  LazyVerticalGrid(
      columns = GridCells.Fixed(gridUiNode.columns),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(gridUiNode.children) { renderChild(it) }
  }
}
