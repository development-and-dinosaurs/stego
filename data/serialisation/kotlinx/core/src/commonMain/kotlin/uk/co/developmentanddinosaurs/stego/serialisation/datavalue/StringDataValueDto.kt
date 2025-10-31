package uk.co.developmentanddinosaurs.stego.serialisation.datavalue

data class StringDataValueDto(
    val value: String,
) : DataValueDto {
    override fun toDomain(): Any = value
}
