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
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.UiStateMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.module.uiSerializersModule
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.SubmitButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ButtonActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.ButtonUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.ColumnUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.CompositeButtonActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.CompositeValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.GridUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.ImageUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.LabelUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.ProgressIndicatorUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.RowUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.SubmitButtonActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.TextFieldUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.UiNodeMapperRegistry
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.UserInteractionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MaxLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.mapper.MaxLengthValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.mapper.MinLengthValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.mapper.RequiredValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.ValidationRuleMapper

/** Provides the Koin module for UI dependencies. */
class StegoUiKoinModule {
  val module: Module = module {
    includes(StegoCoreKoinModule().module)
    single(named("stegoUiSerializersModule")) { uiSerializersModule() }
    single { UserInteractionMapper() }
    single {
      CompositeButtonActionMapper(
          mappers =
              mapOf(
                  SubmitButtonActionDto::class to SubmitButtonActionMapper(),
              ),
      )
    } bind ButtonActionMapper::class
    single {
      CompositeValidationRuleMapper(
          mappers =
              mapOf(
                  MinLengthValidationRuleDto::class to MinLengthValidationRuleMapper(),
                  MaxLengthValidationRuleDto::class to MaxLengthValidationRuleMapper(),
                  RequiredValidationRuleDto::class to RequiredValidationRuleMapper(),
              ),
      )
    } bind ValidationRuleMapper::class
    single {
      UiNodeMapperRegistry(
          setOf(
              ButtonUiNodeMapper(get()),
              ColumnUiNodeMapper(get()),
              GridUiNodeMapper(get()),
              ImageUiNodeMapper(),
              LabelUiNodeMapper(),
              ProgressIndicatorUiNodeMapper(),
              RowUiNodeMapper(get()),
              TextFieldUiNodeMapper(get(), get()),
          )
      )
    }
    single {
      CompositeStateMapper(
          mapperFactories =
              mapOf(
                  LogicStateDto::class to
                      { stateMapper ->
                        LogicStateMapper(get(), get(), get(), stateMapper)
                      },
                  UiStateDto::class to
                      { stateMapper ->
                        UiStateMapper(stateMapper, get(), get(), get(), get())
                      },
              ),
      )
    }
  }
}
