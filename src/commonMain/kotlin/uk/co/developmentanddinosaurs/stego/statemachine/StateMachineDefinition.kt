package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents the static definition of a state machine.
 *
 * This is a pure data structure, typically deserialized from a configuration file (e.g., JSON).
 * It contains no runtime logic or state, only the blueprint for how the machine should be constructed.
 */
interface StateMachineDefinition {
    /**
     * The ID of the top-level initial [State] that the machine will be in upon starting.
     * This must correspond to a key in the [states] map.
     */
    val initial: String

    /**
     * A map of all top-level states in the state machine, keyed by their unique IDs.
     * For hierarchical states, substates are defined within their parent [State] objects.
     */
    val states: Map<String, State>

    /**
     * The initial data for the machine's [Context].
     * This map will be used to construct the initial [Context] when the machine is started.
     */
    val initialContext: Map<String, Any>
}
