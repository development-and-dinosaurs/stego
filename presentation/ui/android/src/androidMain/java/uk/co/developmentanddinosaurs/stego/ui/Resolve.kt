package uk.co.developmentanddinosaurs.stego.ui

import uk.co.developmentanddinosaurs.stego.statemachine.Context

private val regex = "\\{([^{}]+)\\}".toRegex()

/**
 * Resolves a string containing placeholders (e.g., "{username}") against a state machine Context.
 */
fun resolve(value: String, context: Context): String =
    regex.replace(value) { result ->
        val key = result.groupValues[1]
        context.get(key)?.toString() ?: ""
    }
