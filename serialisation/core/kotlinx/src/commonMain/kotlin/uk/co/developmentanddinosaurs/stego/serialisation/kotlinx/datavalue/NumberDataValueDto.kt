package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue

data class NumberDataValueDto(val value: Number) : DataValueDto {
    override fun toDomain(): Any = value
}
