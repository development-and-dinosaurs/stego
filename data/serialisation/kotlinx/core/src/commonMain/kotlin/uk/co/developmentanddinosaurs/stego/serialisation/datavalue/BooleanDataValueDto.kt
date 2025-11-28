package uk.co.developmentanddinosaurs.stego.serialisation.datavalue

data class BooleanDataValueDto(
    val value: Boolean,
) : DataValueDto {
    override fun toDomain(): Any = value
}
