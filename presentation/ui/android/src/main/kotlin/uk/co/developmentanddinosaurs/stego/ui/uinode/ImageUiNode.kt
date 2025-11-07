package uk.co.developmentanddinosaurs.stego.ui.uinode

import androidx.compose.runtime.Composable
import coil3.compose.AsyncImage
import uk.co.developmentanddinosaurs.stego.ui.node.ImageUiNode

@Composable
fun RenderImageUiNode(imageUiNode: ImageUiNode) {
  AsyncImage(
      model = imageUiNode.url,
      contentDescription = imageUiNode.contentDescription,
  )
}
