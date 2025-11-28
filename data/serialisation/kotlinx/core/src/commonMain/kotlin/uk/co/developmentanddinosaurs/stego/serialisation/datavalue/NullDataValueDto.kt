package uk.co.developmentanddinosaurs.stego.serialisation.datavalue

data object NullDataValueDto : DataValueDto {
    override fun toDomain(): Any? = null
}
