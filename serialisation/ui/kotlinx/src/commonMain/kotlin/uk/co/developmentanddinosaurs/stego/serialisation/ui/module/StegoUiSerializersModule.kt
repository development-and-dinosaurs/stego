package uk.co.developmentanddinosaurs.stego.serialisation.ui.module

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.*
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MaxLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto

val stegoUiSerializersModule = SerializersModule {
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
