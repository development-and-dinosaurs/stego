package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

@StegoNode(type = "stego.row")
data class RowUiNode(
    override val id: String,
    val children: List<WeightedChild>,
) : UiNode
