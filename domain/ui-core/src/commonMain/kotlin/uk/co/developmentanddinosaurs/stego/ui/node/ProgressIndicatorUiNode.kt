package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.annotations.StegoNode

@StegoNode(type = "stego.progress_indicator")
class ProgressIndicatorUiNode(
    override val id: String,
) : UiNode
