package uk.co.developmentanddinosaurs.stego.serialisation

/**
 * A serializable, polymorphic representation of an [uk.co.developmentanddinosaurs.stego.statemachine.Action].
 *
 * Library consumers can provide their own implementations and register them with the `SerializersModule`
 * to allow for custom actions defined in JSON.
 */
interface ActionDto
