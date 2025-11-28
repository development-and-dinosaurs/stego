package uk.co.developmentanddinosaurs.stego.ui.node

/**
 * A generic representation of a UI component.
 */
interface UiNode {
    val id: String
}

/**
 * A generic handler for UI interactions that components can invoke.
 * The key identifies the interaction (e.g., "onTextChange"), and the data is any relevant payload.
 */
typealias UserInteractionHandler = (interactionId: String, data: Map<String, Any?>) -> Unit

/**
 * Represents the validity state of all fields in a form, keyed by their component ID.
 */
typealias FormState = Map<String, Boolean>
