package uk.co.developmentanddinosaurs.stego.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import uk.co.developmentanddinosaurs.stego.statemachine.Event

@Composable
fun Render(view: View, onEvent: (Event) -> Unit) {
    when (view) {
        is ColumnView -> {
            Column {
                view.children.forEach {
                    Render(it, onEvent)
                }
            }
        }
        is LabelView -> {
            Text(text = view.text)
        }
        is ButtonView -> {
            Button(onClick = { onEvent(view.onClick) }) {
                Text(text = view.text)
            }
        }
    }
}
