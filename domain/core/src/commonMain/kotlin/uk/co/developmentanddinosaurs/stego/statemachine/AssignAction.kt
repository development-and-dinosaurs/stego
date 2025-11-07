package uk.co.developmentanddinosaurs.stego.statemachine

import uk.co.developmentanddinosaurs.stego.statemachine.valueresolution.ValueProvider

class AssignAction(
    private val key: String,
    private val value: Any?,
) : Action {
  override fun execute(
      context: Context,
      event: Event,
  ): Context {
    val resolve = ValueProvider.resolve(value)
    val resolvedValue = resolve.get(context, event)
    return context.put(key, resolvedValue)
  }
}
