package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.AssignAction

@Serializable
@SerialName("assign")
data class AssignActionDto(val key: String, val value: DataValueDto) : ActionDto {
    override fun toDomain(): Action {
        return AssignAction(key, value.toDomain())
    }
}
