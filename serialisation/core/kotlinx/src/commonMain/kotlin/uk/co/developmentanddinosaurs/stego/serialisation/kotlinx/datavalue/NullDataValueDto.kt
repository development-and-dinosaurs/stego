package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue

data object NullDataValueDto : DataValueDto {
    override fun toDomain(): Any? = null
}
