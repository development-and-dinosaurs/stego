package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents an action that can be executed by the state machine.
 *
 * This is a functional interface to allow library consumers to provide their own custom action
 * implementations. Actions are the primary mechanism for causing side effects and updating the
 * machine's extended state.
 */
fun interface Action {
  /**
   * Executes the logic of the action.
   *
   * Following an immutable pattern, this function takes the current [Context] and the triggering
   * [Event], and should return a new, updated [Context] instance.
   *
   * @param context The current, immutable context of the state machine.
   * @param event The event that triggered the transition and this action.
   * @return A new, updated [Context] instance.
   */
  fun execute(
      context: Context,
      event: Event,
  ): Context
}
