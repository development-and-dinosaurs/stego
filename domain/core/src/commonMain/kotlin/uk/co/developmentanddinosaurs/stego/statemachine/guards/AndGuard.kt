package uk.co.developmentanddinosaurs.stego.statemachine.guards

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

/**
 * A composite guard that returns true only if all of its inner guards evaluate to true.
 * It short-circuits, stopping evaluation as soon as one inner guard returns false.
 */
class AndGuard(
    private vararg val guards: Guard,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean = guards.all { it.evaluate(context, event) }
}
