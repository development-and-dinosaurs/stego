package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue

import kotlinx.serialization.Serializable

@Serializable(with = DataValueDtoSerializer::class)
sealed interface DataValueDto {
    fun toDomain(): Any?
}
