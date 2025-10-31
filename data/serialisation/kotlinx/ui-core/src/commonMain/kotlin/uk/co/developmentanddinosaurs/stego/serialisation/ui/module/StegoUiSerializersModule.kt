package uk.co.developmentanddinosaurs.stego.serialisation.ui.module

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.BypassValidationButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ColumnUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.GridUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ImageUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.LabelUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ProgressIndicatorUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.SubmitButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.TextFieldUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MaxLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto

val stegoUiSerializersModule =
    SerializersModule {
        polymorphic(StateDto::class) {
            subclass(UiStateDto::class)
        }
        polymorphic(UiNodeDto::class) {
            subclass(ColumnUiNodeDto::class)
            subclass(TextFieldUiNodeDto::class)
            subclass(ButtonUiNodeDto::class)
            subclass(ProgressIndicatorUiNodeDto::class)
            subclass(LabelUiNodeDto::class)
            subclass(ImageUiNodeDto::class)
            subclass(GridUiNodeDto::class)
        }
        polymorphic(ButtonActionDto::class) {
            subclass(SubmitButtonActionDto::class)
            subclass(BypassValidationButtonActionDto::class)
        }
        polymorphic(ValidationRuleDto::class) {
            subclass(RequiredValidationRuleDto::class)
            subclass(MinLengthValidationRuleDto::class)
            subclass(MaxLengthValidationRuleDto::class)
        }
    }
