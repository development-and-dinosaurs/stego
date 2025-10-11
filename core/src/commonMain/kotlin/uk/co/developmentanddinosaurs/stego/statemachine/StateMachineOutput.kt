package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a consistent, atomic snapshot of the state machine's output at a given time.
 *
 * @property state The current active [State] of the machine.
 * @property context The current, immutable [Context] of the machine.
 */
data class StateMachineOutput(
    val state: State,
    val context: Context
)
