package uk.co.developmentanddinosaurs.stego.ui

import uk.co.developmentanddinosaurs.stego.annotations.StegoComponent

/**
 * A domain model representing a declarative UI interaction initiated by a user. *
 *
 * @property trigger The unique identifier for the interaction.
 */
@StegoComponent(type = "user-interaction")
data class UserInteraction(
    val trigger: String,
)
