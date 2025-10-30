package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.Serializable

/**
 * DTO for a declarative interaction object from the server.
 *
 * @property trigger The unique identifier for the interaction, which maps to a state machine event type.
 */
@Serializable
data class InteractionDto(val trigger: String)
