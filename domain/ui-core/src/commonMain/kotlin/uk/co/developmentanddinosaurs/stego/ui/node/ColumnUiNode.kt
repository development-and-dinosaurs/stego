package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

data class ColumnUiNode(
    override val id: String,
    val children: List<UiNode>,
) : UiNode
