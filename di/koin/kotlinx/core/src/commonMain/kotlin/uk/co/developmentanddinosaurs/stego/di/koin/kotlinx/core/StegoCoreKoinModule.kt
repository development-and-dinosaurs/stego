package uk.co.developmentanddinosaurs.stego.di.koin.kotlinx.core

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.ActionDtoMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.ActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.AssignActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.LogActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.TransitionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.module.stegoCoreSerializersModule

/**
 * Provides the Koin module for core dependencies.
 */
class StegoCoreKoinModule {
    val module: Module =
        module {
            single(named("stegoCoreSerializersModule")) { stegoCoreSerializersModule }
            single { ActionMapper() } bind ActionDtoMapper::class
            singleOf(::AssignActionMapper)
            single { LogActionMapper { message -> println(message) } }
            singleOf(::TransitionMapper)
        }
}
