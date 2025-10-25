package uk.co.developmentanddinosaurs.stego.ui.node

data class ColumnUiNode(
    override val id: String,
    val children: List<UiNode>
) : UiNode
