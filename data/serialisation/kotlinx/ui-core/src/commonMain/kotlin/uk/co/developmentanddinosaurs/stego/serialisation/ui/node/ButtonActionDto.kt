package uk.co.developmentanddinosaurs.stego.serialisation.ui.node

import kotlinx.serialization.Serializable

/**
 * DTO for a button's action, including its validation strategy.
 */
@Serializable
sealed interface ButtonActionDto {
    val trigger: String
}
