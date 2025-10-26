package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue

data class StringDataValueDto(val value: String) : DataValueDto {
    override fun toDomain(): Any = value
}
