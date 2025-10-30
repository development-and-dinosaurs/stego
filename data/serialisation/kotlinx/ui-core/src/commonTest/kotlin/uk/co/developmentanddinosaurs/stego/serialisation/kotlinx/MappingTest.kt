package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.*
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.*
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.ui.node.*
import uk.co.developmentanddinosaurs.stego.ui.validators.MinLengthValidationRule
import uk.co.developmentanddinosaurs.stego.ui.validators.RequiredValidationRule

class MappingTest : BehaviorSpec({
    val interactionMapper = InteractionMapper()
    val buttonActionMapper = ButtonActionMapper()
    val validationRuleMapper = ValidationRuleMapper()
    val uiNodeMapper = CompositeUiNodeMapper(
        simpleMappers = mapOf(
            LabelUiNodeDto::class to LabelUiNodeMapper(),
            ProgressIndicatorUiNodeDto::class to ProgressIndicatorUiNodeMapper()
        ),
        compositeAwareFactories = mapOf(
            ColumnUiNodeDto::class to { mapper: UiNodeMapper -> ColumnUiNodeMapper(mapper) },
            ButtonUiNodeDto::class to { _: UiNodeMapper -> ButtonUiNodeMapper(buttonActionMapper) },
            TextFieldUiNodeDto::class to { _: UiNodeMapper ->
                TextFieldUiNodeMapper(
                    interactionMapper,
                    validationRuleMapper
                )
            }
        )
    )

    Given("a LabelUiNodeDto") {
        val labelUiNodeDto = LabelUiNodeDto("label-1", "Hello, World!")
        When("it is mapped to a domain object") {
            val uiNode = uiNodeMapper.map(labelUiNodeDto)
            Then("it should be a LabelUiNode") {
                uiNode.shouldBeInstanceOf<LabelUiNode>()
            }
            Then("it should have the correct text") {
                (uiNode as LabelUiNode).text shouldBe "Hello, World!"
            }
        }
    }

    Given("a ButtonUiNodeDto") {
        val buttonUiNodeDto = ButtonUiNodeDto(
            "button-1", "Click me",
            SubmitButtonActionDto(
                trigger = "EVENT_TYPE"
            )
        )
        When("it is mapped to a domain object") {
            val uiNode = uiNodeMapper.map(buttonUiNodeDto)
            Then("it should be a ButtonUiNode") {
                uiNode.shouldBeInstanceOf<ButtonUiNode>()
            }
            Then("it should have the correct text") {
                (uiNode as ButtonUiNode).text shouldBe "Click me"
            }
            Then("it should have the correct onClick action") {
                val action = (uiNode as ButtonUiNode).onClick
                action.shouldBeInstanceOf<SubmitButtonAction>()
                action.trigger shouldBe "EVENT_TYPE"
            }
        }
    }

    Given("a ColumnUiNodeDto") {
        val columnUiNodeDto = ColumnUiNodeDto(
            "column-1",
            children = listOf(
                LabelUiNodeDto("label-in-column", "I am a label"),
                ButtonUiNodeDto("button-in-column", "I am a button", SubmitButtonActionDto("BUTTON_EVENT"))
            )
        )
        When("it is mapped to a domain object") {
            val uiNode = uiNodeMapper.map(columnUiNodeDto)
            Then("it should be a ColumnUiNode") {
                uiNode.shouldBeInstanceOf<ColumnUiNode>()
            }
            Then("it should have the correct number of children") {
                (uiNode as ColumnUiNode).children.size shouldBe 2
            }
            Then("the first child should be a LabelUiNode") {
                (uiNode as ColumnUiNode).children[0].shouldBeInstanceOf<LabelUiNode>()
            }
            Then("the second child should be a ButtonUiNode") {
                (uiNode as ColumnUiNode).children[1].shouldBeInstanceOf<ButtonUiNode>()
            }
        }
    }

    Given("a TextFieldUiNodeDto") {
        val textFieldUiNodeDto = TextFieldUiNodeDto(
            "text-field-1",
            "initial text",
            "label",
            InteractionDto("TEXT_CHANGED"),
            listOf(
                RequiredValidationRuleDto("Required"),
                MinLengthValidationRuleDto("Too short", 5)
            )
        )
        When("it is mapped to a domain object") {
            val uiNode = uiNodeMapper.map(textFieldUiNodeDto)
            Then("it should be a TextFieldUiNode") {
                uiNode.shouldBeInstanceOf<TextFieldUiNode>()
            }
            Then("it should have the correct text") {
                (uiNode as TextFieldUiNode).text shouldBe "initial text"
            }
            Then("it should have the correct label") {
                (uiNode as TextFieldUiNode).label shouldBe "label"
            }
            Then("it should have the correct onTextChanged interaction") {
                (uiNode as TextFieldUiNode).onTextChanged.trigger shouldBe "TEXT_CHANGED"
            }
            Then("it should have the correct validators") {
                val validators = (uiNode as TextFieldUiNode).validation
                validators.size shouldBe 2
                validators[0].shouldBeInstanceOf<RequiredValidationRule>()
                val minLengthValidator = validators[1].shouldBeInstanceOf<MinLengthValidationRule>()
                minLengthValidator.length shouldBe 5
            }
        }
    }

    Given("a ProgressIndicatorUiNodeDto") {
        val progressIndicatorUiNodeDto = ProgressIndicatorUiNodeDto("progress-1")
        When("it is mapped to a domain object") {
            val uiNode = uiNodeMapper.map(progressIndicatorUiNodeDto)
            Then("it should be a ProgressIndicatorUiNode") {
                uiNode.shouldBeInstanceOf<ProgressIndicatorUiNode>()
            }
        }
    }
})