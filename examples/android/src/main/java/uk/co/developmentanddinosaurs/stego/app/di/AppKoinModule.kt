package uk.co.developmentanddinosaurs.stego.app.di

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import uk.co.developmentanddinosaurs.stego.app.LoginInvokable
import uk.co.developmentanddinosaurs.stego.app.LoginViewModel
import uk.co.developmentanddinosaurs.stego.app.stateDef
import uk.co.developmentanddinosaurs.stego.di.koin.kotlinx.ui.StegoUiKoinModule
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.InvokableDefinitionMapper
import uk.co.developmentanddinosaurs.stego.statemachine.Invokable

val appModule = module {
    includes(StegoUiKoinModule().module)

    single {
        val stegoCoreSerializersModule: SerializersModule = get(named("stegoCoreSerializersModule"))
        val stegoUiSerializersModule: SerializersModule = get(named("stegoUiSerializersModule"))
        Json {
            serializersModule = stegoCoreSerializersModule + stegoUiSerializersModule
        }
    }

    single {
        val invokables = mapOf<String, Invokable>("LoginInvokable" to LoginInvokable)
        InvokableDefinitionMapper(invokables)
    }

    viewModel {
        val definition = stateDef(get(), get())
        LoginViewModel(definition)
    }
}
