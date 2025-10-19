package uk.co.developmentanddinosaurs.stego.statemachine.valueresolution

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

/**
 * A [ValueProvider] that retrieves a value from the [Event] using a key.
 * @property key The key to look up in the event.
 */
data class EventValue(private val key: String) : ValueProvider {
    override fun get(context: Context, event: Event?): Any? = event?.data[key]
}
