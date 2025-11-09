package uk.co.developmentanddinosaurs.stego.ui.node

import uk.co.developmentanddinosaurs.stego.annotations.StegoComponent
import uk.co.developmentanddinosaurs.stego.ui.ButtonAction

@StegoComponent("bypass_validation")
data class BypassValidationButtonAction(
    override val trigger: String,
) : ButtonAction
