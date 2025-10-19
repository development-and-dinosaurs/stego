package uk.co.developmentanddinosaurs.stego.ui

import uk.co.developmentanddinosaurs.stego.statemachine.Context

internal fun resolve(text: String, context: Context): String {
    val regex = "\\$\\{([^\\}]+)\\}".toRegex()
    return regex.replace(text) { matchResult ->
        val key = matchResult.groupValues[1]
        (context.get(key) as CharSequence?)?: ""
    }
}
