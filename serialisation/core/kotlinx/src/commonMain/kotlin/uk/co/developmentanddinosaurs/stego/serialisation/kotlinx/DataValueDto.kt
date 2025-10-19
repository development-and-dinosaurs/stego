package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = DataValueDtoSerializer::class)
sealed interface DataValueDto {
    fun toDomain(): Any?
}

data class StringDataValueDto(val value: String) : DataValueDto {
    override fun toDomain(): Any = value
}

data class NumberDataValueDto(val value: Number) : DataValueDto {
    override fun toDomain(): Any = value
}

data class BooleanDataValueDto(val value: Boolean) : DataValueDto {
    override fun toDomain(): Any = value
}

data object NullDataValueDto : DataValueDto {
    override fun toDomain(): Any? = null
}

object DataValueDtoSerializer : KSerializer<DataValueDto> {
    override val descriptor: SerialDescriptor = JsonPrimitive.serializer().descriptor

    override fun serialize(encoder: Encoder, value: DataValueDto) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("This serializer can only be used with JSON")
        val jsonElement = when (value) {
            is StringDataValueDto -> JsonPrimitive(value.value)
            is NumberDataValueDto -> JsonPrimitive(value.value)
            is BooleanDataValueDto -> JsonPrimitive(value.value)
            is NullDataValueDto -> JsonNull
        }
        jsonEncoder.encodeJsonElement(jsonElement)
    }

    override fun deserialize(decoder: Decoder): DataValueDto {
        val jsonDecoder = decoder as? JsonDecoder ?: error("This serializer can only be used with JSON")
        val jsonElement = jsonDecoder.decodeJsonElement() as JsonPrimitive

        return when {
            jsonElement is JsonNull -> NullDataValueDto
            jsonElement.isString -> StringDataValueDto(jsonElement.content)
            jsonElement.longOrNull != null -> NumberDataValueDto(jsonElement.longOrNull!!)
            jsonElement.doubleOrNull != null -> NumberDataValueDto(jsonElement.doubleOrNull!!)
            jsonElement.booleanOrNull != null -> BooleanDataValueDto(jsonElement.booleanOrNull!!)
            else -> error("Unsupported JSON primitive type for DataValueDto")
        }
    }
}