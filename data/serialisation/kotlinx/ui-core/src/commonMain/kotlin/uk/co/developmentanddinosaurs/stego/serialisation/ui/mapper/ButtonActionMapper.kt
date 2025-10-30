package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.BypassValidationButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.SubmitButtonActionDto
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonAction
import uk.co.developmentanddinosaurs.stego.ui.node.BypassValidationButtonAction
import uk.co.developmentanddinosaurs.stego.ui.node.SubmitButtonAction

class ButtonActionMapper {
    fun map(dto: ButtonActionDto): ButtonAction = when (dto) {
        is SubmitButtonActionDto -> SubmitButtonAction(
            trigger = dto.trigger,
            validationScope = dto.validationScope,
            onValidationFail = dto.onValidationFail
        )

        is BypassValidationButtonActionDto -> BypassValidationButtonAction(trigger = dto.trigger)
    }
}