package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

@StegoNode(type = "stego.column")
data class ColumnUiNode(
    override val id: String,
    val children: List<UiNode>,
) : UiNode
