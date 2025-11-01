package uk.co.developmentanddinosaurs.stego.statemachine.guards

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

val TrueGuard =
    object : Guard {
        override fun evaluate(context: Context, event: Event) = true
    }

val FalseGuard =
    object : Guard {
        override fun evaluate(context: Context, event: Event) = false
    }
