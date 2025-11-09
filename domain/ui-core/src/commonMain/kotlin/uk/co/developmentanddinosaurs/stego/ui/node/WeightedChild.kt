package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

@StegoNode(type = "weighted-child")
data class WeightedChild(override val id: String,val weight: Float, val child: UiNode) : DecoratedUiNode
