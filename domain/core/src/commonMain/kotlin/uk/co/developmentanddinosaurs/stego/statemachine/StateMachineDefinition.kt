package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents the static definition of a state machine.
 *
 * This is a pure data structure, typically deserialized from a configuration file (e.g., JSON). It
 * contains no runtime logic or state, only the blueprint for how the machine should be constructed.
 *
 * @property initial The ID of the top-level initial [State] that the machine will be in upon
 *   starting.
 * @property states A map of all top-level states in the state machine, keyed by their unique IDs.
 * @property initialContext The initial [Context] of the state machine.
 */
data class StateMachineDefinition(
    val initial: String,
    val states: Map<String, State>,
    val initialContext: Map<String, Any?> = emptyMap(),
)
