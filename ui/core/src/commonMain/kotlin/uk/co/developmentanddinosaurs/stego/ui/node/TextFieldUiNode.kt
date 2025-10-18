package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.statemachine.Event

data class TextFieldUiNode(
    val text: String,
    val label: String,
    val onTextChanged: Event
) : UiNode
