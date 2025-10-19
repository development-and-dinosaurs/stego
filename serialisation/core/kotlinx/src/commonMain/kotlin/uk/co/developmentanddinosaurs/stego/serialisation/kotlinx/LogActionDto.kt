package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.LogAction

@Serializable
@SerialName("log")
data class LogActionDto(val message: String) : ActionDto {
    fun toDomain(): Action {
        return LogAction(message, ::println)
    }
}
