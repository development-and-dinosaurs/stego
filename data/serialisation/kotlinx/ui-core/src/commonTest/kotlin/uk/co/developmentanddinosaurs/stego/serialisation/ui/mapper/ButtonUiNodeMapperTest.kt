package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.BypassValidationButtonActionDto
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.BypassValidationButtonAction

class ButtonUiNodeMapperTest :
    BehaviorSpec({
        Given("a ButtonUiNodeMapper") {
            val mapper = ButtonUiNodeMapper(ButtonActionMapper())

            and("a ButtonUiNodeDto") {
                val dto =
                    ButtonUiNodeDto(
                        id = "button-id",
                        text = "Click Me",
                        onClick = BypassValidationButtonActionDto("some-screen"),
                    )

                When("the dto is mapped") {
                    val uiNode = mapper.map(dto)

                    Then("it should return a ButtonUiNode") {
                        uiNode.shouldBeInstanceOf<ButtonUiNode>()
                    }

                    Then("it should have the correct id") {
                        uiNode.id shouldBe "button-id"
                    }

                    Then("it should have the correct text") {
                        uiNode.text shouldBe "Click Me"
                    }

                    Then("it should have the correctly mapped onClick action") {
                        val action = uiNode.onClick
                        action.shouldBeInstanceOf<BypassValidationButtonAction>()
                    }
                }
            }

            and("a non-ButtonUiNodeDto") {
                val dto = OtherUiNodeDto

                When("the dto is mapped") {
                    Then("it should throw an IllegalArgumentException") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                mapper.map(dto)
                            }
                        exception.message shouldBe "Failed requirement."
                    }
                }
            }
        }
    })
