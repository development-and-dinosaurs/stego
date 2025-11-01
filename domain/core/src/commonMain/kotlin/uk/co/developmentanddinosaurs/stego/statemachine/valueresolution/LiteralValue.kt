package uk.co.developmentanddinosaurs.stego.statemachine.valueresolution

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

/**
 * A [ValueProvider] that holds a static, literal value.
 * @property value The literal value.
 */
class LiteralValue(
    private val value: Any?,
) : ValueProvider {
    override fun get(context: Context, event: Event?): Any? = value
}
