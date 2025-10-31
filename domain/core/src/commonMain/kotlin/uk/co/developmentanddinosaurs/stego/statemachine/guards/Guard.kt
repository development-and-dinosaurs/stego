package uk.co.developmentanddinosaurs.stego.statemachine.guards

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

fun interface Guard {
    fun evaluate(
        context: Context,
        event: Event,
    ): Boolean

    companion object {
        fun create(expression: String): Guard = GuardParser.parse(expression)
    }
}
