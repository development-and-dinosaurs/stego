package uk.co.developmentanddinosaurs.stego.statemachine

import uk.co.developmentanddinosaurs.stego.statemachine.guards.Guard

/**
 * Represents a directed transition between two [State]s in the state machine.
 *
 * A transition is triggered by an [Event] and is only taken if its optional [guard] condition is met.
 * When taken, it will execute a list of [Action]s before entering the target state.
 *
 * @property target The ID of the target [State] to transition to.
 * @property actions A list of [Action]s to be executed when this transition is taken.
 * @property guard An optional [Guard] condition that must be met for the transition to be taken.
 */
data class Transition(
    val target: String,
    val actions: List<Action> = emptyList(),
    val guard: Guard? = null,
)
