package uk.co.developmentanddinosaurs.stego.statemachine

class AssignAction(private val key: String, private val value: DataValue) : Action {
    override fun execute(
        context: Context,
        event: Event,
    ): Context {
        return context.put(key, value)
    }
}
