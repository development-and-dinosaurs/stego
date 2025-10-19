package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.*
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.*
import uk.co.developmentanddinosaurs.stego.ui.node.*

class MappingTest : BehaviorSpec({
    val uiNodeMapper = CompositeUiNodeMapper(
        simpleMappers = mapOf(
            LabelUiNodeDto::class to LabelUiNodeMapper(),
            ProgressIndicatorUiNodeDto::class to ProgressIndicatorUiNodeMapper()
        ),
        compositeAwareFactories = mapOf(
            ColumnUiNodeDto::class to { mapper: UiNodeMapper -> ColumnUiNodeMapper(mapper) },
            ButtonUiNodeDto::class to { _: UiNodeMapper -> ButtonUiNodeMapper() },
            TextFieldUiNodeDto::class to { _: UiNodeMapper -> TextFieldUiNodeMapper() }
        )
    )

    Given("a LabelUiNodeDto") {
        val labelUiNodeDto = LabelUiNodeDto("Hello, World!")
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
        val buttonUiNodeDto = ButtonUiNodeDto("Click me", EventDto("EVENT_TYPE"))
        When("it is mapped to a domain object") {
            val uiNode = uiNodeMapper.map(buttonUiNodeDto)
            Then("it should be a ButtonUiNode") {
                uiNode.shouldBeInstanceOf<ButtonUiNode>()
            }
            Then("it should have the correct text") {
                (uiNode as ButtonUiNode).text shouldBe "Click me"
            }
            Then("it should have the correct onClick event") {
                (uiNode as ButtonUiNode).onClick.type shouldBe "EVENT_TYPE"
            }
        }
    }

    Given("a ColumnUiNodeDto") {
        val columnUiNodeDto = ColumnUiNodeDto(
            children = listOf(
                LabelUiNodeDto("I am a label"),
                ButtonUiNodeDto("I am a button", EventDto("BUTTON_EVENT"))
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
        val textFieldUiNodeDto = TextFieldUiNodeDto("initial text", "label", EventDto("TEXT_CHANGED"))
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
            Then("it should have the correct onTextChanged event") {
                (uiNode as TextFieldUiNode).onTextChanged.type shouldBe "TEXT_CHANGED"
            }
        }
    }

    Given("a ProgressIndicatorUiNodeDto") {
        val progressIndicatorUiNodeDto = ProgressIndicatorUiNodeDto()
        When("it is mapped to a domain object") {
            val uiNode = uiNodeMapper.map(progressIndicatorUiNodeDto)
            Then("it should be a ProgressIndicatorUiNode") {
                uiNode.shouldBeInstanceOf<ProgressIndicatorUiNode>()
            }
        }
    }
})