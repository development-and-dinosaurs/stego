package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.ContextReference
import uk.co.developmentanddinosaurs.stego.statemachine.EventReference
import uk.co.developmentanddinosaurs.stego.statemachine.LiteralReference
import uk.co.developmentanddinosaurs.stego.statemachine.ValueReference

@Serializable
sealed interface ValueReferenceDto {
    fun toDomain(): ValueReference
}

@Serializable
data class ContextReferenceDto(
    val path: String
) : ValueReferenceDto {
    override fun toDomain(): ValueReference = ContextReference(path)
}

@Serializable
data class EventReferenceDto(
    val path: String
) : ValueReferenceDto {
    override fun toDomain(): ValueReference = EventReference(path)
}

@Serializable
data class LiteralReferenceDto(
    val value: DataValueDto
) : ValueReferenceDto {
    override fun toDomain(): ValueReference = LiteralReference(value.toDomain())
}
