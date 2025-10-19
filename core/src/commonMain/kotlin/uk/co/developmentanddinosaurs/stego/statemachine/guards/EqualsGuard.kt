package uk.co.developmentanddinosaurs.stego.statemachine.guards

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

/**
 * A guard that checks if a 'left' value is equal to a 'right' value.
 */
class EqualsGuard(
    private val left: Any,
    private val right: Any
) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return performEqualityCheck(left, right, context, event)
    }
}
