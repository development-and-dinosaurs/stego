package uk.co.developmentanddinosaurs.stego.statemachine.guards

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

/** A composite guard that negates the result of its inner guard. */
class NotGuard(
    private val guard: Guard,
) : Guard {
  override fun evaluate(
      context: Context,
      event: Event,
  ): Boolean = !guard.evaluate(context, event)
}
