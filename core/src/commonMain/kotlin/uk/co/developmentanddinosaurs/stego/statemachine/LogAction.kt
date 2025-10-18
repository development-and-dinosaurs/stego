package uk.co.developmentanddinosaurs.stego.statemachine

class LogAction(
    private val message: String,
    private val logger: (String) -> Unit,
) : Action {
    override fun execute(
        context: Context,
        event: Event,
    ): Context {
        logger("LogAction: $message")
        return context
    }
}
