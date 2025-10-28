package uk.co.developmentanddinosaurs.stego.serialisation

import kotlinx.serialization.Serializable

@Serializable
data class TransitionDto(
    val target: String,
    val actions: List<ActionDto> = emptyList(),
    val guard: String? = null
)
