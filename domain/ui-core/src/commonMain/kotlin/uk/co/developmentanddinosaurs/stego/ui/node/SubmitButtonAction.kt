package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.annotations.StegoComponent
import uk.co.developmentanddinosaurs.stego.ui.ButtonAction

@StegoComponent("submit")
data class SubmitButtonAction(
    override val trigger: String,
    val validationScope: List<String>? = null,
    val onValidationFail: String?,
) : ButtonAction
