package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.InteractionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.TextFieldUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.ui.node.TextFieldUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UserInteraction
import uk.co.developmentanddinosaurs.stego.ui.validators.MinLengthValidationRule

class TextFieldUiNodeMapperTest :
    BehaviorSpec({
        Given("a TextFieldUiNodeMapper") {
            val interactionMapper = InteractionMapper()
            val validationRuleMapper = ValidationRuleMapper()
            val mapper = TextFieldUiNodeMapper(interactionMapper, validationRuleMapper)

            and("a TextFieldUiNodeDto") {
                val dto =
                    TextFieldUiNodeDto(
                        id = "text-field-id",
                        text = "Initial text",
                        label = "Enter text",
                        onTextChanged = InteractionDto(trigger = "text-changed-event"),
                        validators = listOf(MinLengthValidationRuleDto(message = "Too short", length = 5)),
                    )

                When("the dto is mapped") {
                    val uiNode = mapper.map(dto)

                    Then("it should map all properties correctly") {
                        uiNode.shouldBeInstanceOf<TextFieldUiNode>()
                        uiNode.id shouldBe "text-field-id"
                        uiNode.text shouldBe "Initial text"
                        uiNode.label shouldBe "Enter text"

                        uiNode.onTextChanged.shouldBeInstanceOf<UserInteraction>()
                        uiNode.onTextChanged.trigger shouldBe "text-changed-event"

                        uiNode.validation shouldHaveSize 1
                        uiNode.validation[0] shouldBe MinLengthValidationRule("Too short", 5)
                    }
                }
            }

            and("a non-TextFieldUiNodeDto") {
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
