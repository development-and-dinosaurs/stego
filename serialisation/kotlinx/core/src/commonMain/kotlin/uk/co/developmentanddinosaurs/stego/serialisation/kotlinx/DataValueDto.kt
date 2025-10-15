package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.*

@Serializable
sealed interface DataValueDto {
    fun toDomain(): DataValue
}

@Serializable
sealed interface PrimitiveDto : DataValueDto {
    override fun toDomain(): Primitive
}

@Serializable
data class StringPrimitiveDto(val value: String) : PrimitiveDto {
    override fun toDomain(): Primitive = StringPrimitive(value)
}

@Serializable
data class IntPrimitiveDto(val value: Int) : PrimitiveDto {
    override fun toDomain(): Primitive = IntPrimitive(value)
}

@Serializable
data class LongPrimitiveDto(val value: Long) : PrimitiveDto {
    override fun toDomain(): Primitive = LongPrimitive(value)
}

@Serializable
data class FloatPrimitiveDto(val value: Float) : PrimitiveDto {
    override fun toDomain(): Primitive = FloatPrimitive(value)
}

@Serializable
data class DoublePrimitiveDto(val value: Double) : PrimitiveDto {
    override fun toDomain(): Primitive = DoublePrimitive(value)
}

@Serializable
data class BooleanPrimitiveDto(val value: Boolean) : PrimitiveDto {
    override fun toDomain(): Primitive = BooleanPrimitive(value)
}

@Serializable
data class ObjectValueDto(val value: Map<String, DataValueDto>) : DataValueDto {
    override fun toDomain(): DataValue = ObjectValue(value.mapValues { it.value.toDomain() })
}

@Serializable
data class ListValueDto(val value: List<DataValueDto>) : DataValueDto {
    override fun toDomain(): DataValue = ListValue(value.map { it.toDomain() })
}
