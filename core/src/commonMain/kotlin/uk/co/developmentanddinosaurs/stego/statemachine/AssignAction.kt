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
        println(event)
        val resolve = ValueProvider.resolve(value)
        println(resolve)
        val resolvedValue = resolve.get(context, event)
        println("AssignAction: $key = $resolvedValue")
        return context.put(key, resolvedValue)
    }
}
