package uk.co.developmentanddinosaurs.stego.serialisation.module

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.developmentanddinosaurs.stego.serialisation.ActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.AssignActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.LogActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.LogicStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.StateDto

val stegoCoreSerializersModule = SerializersModule {
  polymorphic(StateDto::class) { subclass(LogicStateDto::class) }
  polymorphic(ActionDto::class) {
    subclass(LogActionDto::class)
    subclass(AssignActionDto::class)
  }
}
