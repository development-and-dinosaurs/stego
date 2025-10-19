package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class InvokableDto(
    val id: String,
    val src: String,
    val input: Map<String, JsonElement>? = null
)
