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
import uk.co.developmentanddinosaurs.stego.serialisation.ui.module.stegoUiSerializersModule
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ColumnUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.GridUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ImageUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.LabelUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ProgressIndicatorUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.SubmitButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.TextFieldUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.ButtonActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.ButtonUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.ColumnUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.CompositeButtonActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.CompositeUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.CompositeValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.GridUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.ImageUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.LabelUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.ProgressIndicatorUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.SubmitButtonActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.TextFieldUiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.UiNodeMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper.UserInteractionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MaxLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.mapper.MaxLengthValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.mapper.MinLengthValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.mapper.RequiredValidationRuleMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.mapper.ValidationRuleMapper

/** Provides the Koin module for UI dependencies. */
class StegoUiKoinModule {
  val module: Module = module {
    includes(StegoCoreKoinModule().module)
    single(named("stegoUiSerializersModule")) { stegoUiSerializersModule }
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
      CompositeUiNodeMapper(
          simpleMappers =
              mapOf(
                  LabelUiNodeDto::class to LabelUiNodeMapper(),
                  ProgressIndicatorUiNodeDto::class to ProgressIndicatorUiNodeMapper(),
                  TextFieldUiNodeDto::class to TextFieldUiNodeMapper(get(), get()),
                  ButtonUiNodeDto::class to ButtonUiNodeMapper(get()),
                  ImageUiNodeDto::class to ImageUiNodeMapper(),
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
