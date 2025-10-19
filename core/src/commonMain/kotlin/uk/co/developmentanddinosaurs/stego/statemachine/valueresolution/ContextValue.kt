package uk.co.developmentanddinosaurs.stego.statemachine.valueresolution

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

/**
 * A [ValueProvider] that retrieves a value from the [Context] using a key.
 * @property key The key to look up in the context.
 */
data class ContextValue(private val key: String) : ValueProvider {
    override fun get(context: Context, event: Event?): Any? = context.get(key)
}
