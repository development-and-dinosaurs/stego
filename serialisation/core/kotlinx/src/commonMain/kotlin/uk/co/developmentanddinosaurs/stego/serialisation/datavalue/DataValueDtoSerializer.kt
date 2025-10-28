package uk.co.developmentanddinosaurs.stego.serialisation.datavalue

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

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
        return when (val jsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonNull -> NullDataValueDto
            is JsonPrimitive -> when {
                jsonElement.isString -> StringDataValueDto(jsonElement.content)
                jsonElement.longOrNull != null -> NumberDataValueDto(jsonElement.longOrNull!!)
                jsonElement.doubleOrNull != null -> NumberDataValueDto(jsonElement.doubleOrNull!!)
                jsonElement.booleanOrNull != null -> BooleanDataValueDto(jsonElement.booleanOrNull!!)
                else -> error("Unsupported JSON primitive type for DataValueDto")
            }

            else -> error("DataValueDto can only be deserialized from a JSON primitive or null")
        }
    }
}
