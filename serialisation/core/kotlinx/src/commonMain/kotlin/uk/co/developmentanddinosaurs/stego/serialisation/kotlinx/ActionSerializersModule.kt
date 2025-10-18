package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val actionSerializersModule = SerializersModule {
    polymorphic(ActionDto::class) {
        subclass(LogActionDto::class)
    }
}
