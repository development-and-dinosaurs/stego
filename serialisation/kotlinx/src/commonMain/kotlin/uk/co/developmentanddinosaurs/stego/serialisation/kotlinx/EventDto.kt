package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.Event

@Serializable
data class EventDto(
    val type: String,
    val data: Map<String, DataValueDto> = emptyMap()
) {
    fun toDomain(): Event = Event(
        type = type,
        data = data.mapValues { (_, dataValueDto) -> dataValueDto.toDomain() }
    )
}
