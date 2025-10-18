package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.statemachine.Event

data class ButtonUiNode(
    val text: String,
    val onClick: Event
) : UiNode
