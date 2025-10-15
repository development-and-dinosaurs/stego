package uk.co.developmentanddinosaurs.stego.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.StringPrimitive

@Composable
fun Render(view: View, context: Context, onEvent: (Event) -> Unit) {
    when (view) {
        is ColumnView -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                view.children.forEach {
                    Render(it, context, onEvent)
                }
            }
        }

        is LabelView -> {
            Text(text = resolve(view.text, context))
        }

        is ButtonView -> {
            Button(onClick = { onEvent(view.onClick) }) {
                Text(text = resolve(view.text, context))
            }
        }

        is TextFieldView -> {
            val resolvedText = resolve(view.text, context)
            var text by remember { mutableStateOf(resolvedText) }

            LaunchedEffect(resolvedText) {
                if (text != resolvedText) {
                    text = resolvedText
                }
            }

            TextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    val eventData = view.onTextChanged.data.toMutableMap()
                    eventData["text"] = StringPrimitive(newText)
                    onEvent(view.onTextChanged.copy(data = eventData))
                },
                label = { Text(resolve(view.label, context)) }
            )
        }

        is ProgressIndicatorView -> {
            CircularProgressIndicator()
        }
    }
}

private fun resolve(text: String, context: Context): String {
    val regex = "\\$\\{([^\\}]+)\\}".toRegex()
    return regex.replace(text) { matchResult ->
        val key = matchResult.groupValues[1]
        (context.get(key) as? StringPrimitive)?.value ?: ""
    }
}
