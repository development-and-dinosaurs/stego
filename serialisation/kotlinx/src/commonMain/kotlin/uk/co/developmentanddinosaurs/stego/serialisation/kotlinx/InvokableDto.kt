package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable

@Serializable
data class InvokableDto(
    val src: String,
    val onDone: EventDto,
    val onError: EventDto
)
