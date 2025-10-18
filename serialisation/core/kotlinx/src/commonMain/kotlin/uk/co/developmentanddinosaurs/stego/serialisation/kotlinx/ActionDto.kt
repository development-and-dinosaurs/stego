package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action

/**
 * A serializable, polymorphic representation of an [Action].
 *
 * Library consumers can provide their own implementations and register them with the `SerializersModule`
 * to allow for custom actions defined in JSON.
 */
interface ActionDto
