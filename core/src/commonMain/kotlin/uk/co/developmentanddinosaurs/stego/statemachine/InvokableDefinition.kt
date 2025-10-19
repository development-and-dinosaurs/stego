package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * A definition of a long-running service to be invoked by a [State].
 *
 * @property id A unique identifier for this specific invocation.
 * @property src The [Invokable] logic to be executed.
 * @property input A map of parameters to be passed to the [Invokable] during execution. The values
 * must conform to the [DataValue] model.
 */
data class InvokableDefinition(
    val id: String,
    val src: Invokable,
    val input: Map<String, Any> = emptyMap(),
)
