package uk.co.developmentanddinosaurs.stego.ui

import uk.co.developmentanddinosaurs.stego.statemachine.Event

sealed interface View

data class ColumnView(
    val children: List<View>
) : View

data class LabelView(
    val text: String
) : View

data class ButtonView(
    val text: String,
    val onClick: Event
) : View

data class TextFieldView(
    val text: String,
    val label: String,
    val onTextChanged: Event
) : View

object ProgressIndicatorView : View
