package uk.co.developmentanddinosaurs.stego.di.koin.kotlinx.ui

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import uk.co.developmentanddinosaurs.stego.di.koin.kotlinx.core.StegoCoreKoinModule
import uk.co.developmentanddinosaurs.stego.serialisation.LogicStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.CompositeStateMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.LogicStateMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ButtonActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ButtonUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ColumnUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.CompositeUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.GridUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ImageUiNodeDtoMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.InteractionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.LabelUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ProgressIndicatorUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.TextFieldUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.UiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.UiStateMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.module.stegoUiSerializersModule
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ColumnUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.GridUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ImageUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.LabelUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ProgressIndicatorUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.TextFieldUiNodeDto

/**
 * Provides the Koin module for UI dependencies.
 */
class StegoUiKoinModule {
    val module: Module =
        module {
            includes(StegoCoreKoinModule().module)
            single(named("stegoUiSerializersModule")) { stegoUiSerializersModule }
            single { InteractionMapper() }
            single { ButtonActionMapper() }
            single { ValidationRuleMapper() }
            single {
                CompositeUiNodeMapper(
                    simpleMappers =
                    mapOf(
                        LabelUiNodeDto::class to LabelUiNodeMapper(),
                        ProgressIndicatorUiNodeDto::class to ProgressIndicatorUiNodeMapper(),
                        TextFieldUiNodeDto::class to TextFieldUiNodeMapper(get(), get()),
                        ButtonUiNodeDto::class to ButtonUiNodeMapper(get()),
                        ImageUiNodeDto::class to ImageUiNodeDtoMapper(),
                    ),
                    compositeAwareFactories =
                    mapOf(
                        ColumnUiNodeDto::class to { mapper -> ColumnUiNodeMapper(mapper) },
                        GridUiNodeDto::class to { mapper -> GridUiNodeMapper(mapper) },
                    ),
                )
            } bind UiNodeMapper::class
            single {
                CompositeStateMapper(
                    mapperFactories =
                    mapOf(
                        LogicStateDto::class to { stateMapper ->
                            LogicStateMapper(get(), get(), get(), stateMapper)
                        },
                        UiStateDto::class to { stateMapper ->
                            UiStateMapper(stateMapper, get(), get(), get(), get())
                        },
                    ),
                )
            }
        }
}
