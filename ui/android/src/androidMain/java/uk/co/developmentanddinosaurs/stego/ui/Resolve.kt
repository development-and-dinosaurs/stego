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

/**
 * Resolves placeholders within the values of a map against a simple key-value map.
 * This is useful for creating event data from a component's local state.
 *
 * Example: resolve({"key": "{placeholder}"}, mapOf("placeholder" to "value")) -> {"key": "value"}
 */
fun resolve(map: Map<String, Any?>, localContext: Map<String, Any?>): Map<String, Any?> =
    map.mapValues { (_, value) ->
        if (value !is String) {
            value
        } else {
            regex.replace(value) { result ->
                val key = result.groupValues[1]
                localContext[key]?.toString() ?: ""
            }
        }
    }