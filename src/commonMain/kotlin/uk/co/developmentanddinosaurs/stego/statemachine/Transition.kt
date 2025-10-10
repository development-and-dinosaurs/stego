package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a directed transition between two [State]s in the state machine.
 *
 * A transition is triggered by an [Event] and is only taken if its optional [guard] condition is met.
 * When taken, it will execute a list of [Action]s before entering the target state.
 */
interface Transition {
    /**
     * The ID of the target [State] to transition to.
     * The state machine engine is responsible for resolving this ID to an actual state object.
     */
    val target: String

    /**
     * A list of [Action]s to be executed when this transition is taken.
     * These actions are typically executed after exiting the source state and before entering the target state.
     */
    val actions: List<Action>

    /**
     * An optional [Guard] condition that must be met for the transition to be taken.
     * If the guard is null, it is considered to be always true.
     * If the guard evaluates to false, the transition is blocked.
     */
    val guard: Guard?
}
