package uk.co.developmentanddinosaurs.stego.serialisation.module

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.developmentanddinosaurs.stego.serialisation.*


val stegoCoreSerializersModule = SerializersModule {
    polymorphic(StateDto::class) {
        subclass(LogicStateDto::class)
    }
    polymorphic(ActionDto::class) {
        subclass(LogActionDto::class)
        subclass(AssignActionDto::class)
    }
}
