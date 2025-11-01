package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.BypassValidationButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.SubmitButtonActionDto
import uk.co.developmentanddinosaurs.stego.ui.node.BypassValidationButtonAction
import uk.co.developmentanddinosaurs.stego.ui.node.SubmitButtonAction

class ButtonActionMapperTest : BehaviorSpec({
    Given("a ButtonActionMapper") {
        val mapper = ButtonActionMapper()

        When("mapping a SubmitActionDto") {
            val dto =
                SubmitButtonActionDto(
                    trigger = "submit-event",
                    validationScope = listOf("screen"),
                    onValidationFail = "show-error",
                )
            val action = mapper.map(dto)

            Then("it should return a SubmitAction with correct properties") {
                action.shouldBeInstanceOf<SubmitButtonAction>()
                action.trigger shouldBe "submit-event"
                action.validationScope shouldBe listOf("screen")
                action.onValidationFail shouldBe "show-error"
            }
        }

        When("mapping a BypassValidationButtonActionDto") {
            val dto = BypassValidationButtonActionDto(trigger = "navigate-event")
            val action = mapper.map(dto)

            Then("it should return a NavigateAction with correct properties") {
                action.shouldBeInstanceOf<BypassValidationButtonAction>()
                action.trigger shouldBe "navigate-event"
            }
        }
    }
})
