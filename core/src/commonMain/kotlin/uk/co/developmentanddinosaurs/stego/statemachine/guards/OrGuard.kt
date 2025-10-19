package uk.co.developmentanddinosaurs.stego.statemachine.guards

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

/**
 * A composite guard that returns true if any of its inner guards evaluate to true.
 * It short-circuits, stopping evaluation as soon as one inner guard returns true.
 */
class OrGuard(private vararg val guards: Guard) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return guards.any { it.evaluate(context, event) }
    }
}
