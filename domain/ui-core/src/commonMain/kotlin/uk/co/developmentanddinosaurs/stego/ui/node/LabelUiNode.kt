package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

@StegoNode(type = "stego.label")
data class LabelUiNode(
    override val id: String,
    val text: String,
) : UiNode
