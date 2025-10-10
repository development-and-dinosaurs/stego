package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents an action that can be executed by the state machine.
 *
 * Actions are the primary mechanism for causing side effects and updating the machine's extended state.
 * Following an immutable pattern, an action takes the current [Context] and the triggering [Event],
 * and returns a new, updated [Context].
 */
interface Action {
    /**
     * Executes the logic of the action.
     *
     * @param context The current, immutable context of the state machine.
     * @param event The event that triggered the transition and this action.
     * @return A new, updated [Context] reflecting any changes made by this action.
     */
    fun execute(context: Context, event: Event): Context
}
