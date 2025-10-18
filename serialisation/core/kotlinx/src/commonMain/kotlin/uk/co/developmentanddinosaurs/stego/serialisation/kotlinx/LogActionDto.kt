package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable

@Serializable
data class LogActionDto(val message: String) : ActionDto
