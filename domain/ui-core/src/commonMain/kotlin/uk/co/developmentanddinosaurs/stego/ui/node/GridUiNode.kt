package uk.co.developmentanddinosaurs.stego.ui.node

/**
 * UI model for a grid layout.
 *
 * @property id A unique identifier for this component.
 * @property columns The number of columns in the grid.
 * @property children The list of [UiNode]s to display within the grid.
 */
data class GridUiNode(
    override val id: String,
    val columns: Int,
    val children: List<UiNode>,
) : UiNode
