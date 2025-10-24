package uk.co.developmentanddinosaurs.stego.statemachine.guards

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

/**
 * A guard that checks if a 'left' value is less than a 'right' value.
 */
class LessThanGuard(
    private val left: Any,
    private val right: Any,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean = performComparison(left, right, context, event) { l, r -> l < r }
}
