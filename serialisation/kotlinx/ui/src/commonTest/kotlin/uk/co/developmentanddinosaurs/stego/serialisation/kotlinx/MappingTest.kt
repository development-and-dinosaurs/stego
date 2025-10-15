package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.ui.ButtonView
import uk.co.developmentanddinosaurs.stego.ui.ColumnView
import uk.co.developmentanddinosaurs.stego.ui.LabelView
import uk.co.developmentanddinosaurs.stego.ui.UiState

class MappingTest : BehaviorSpec({
    Given("a LabelViewDto") {
        val labelViewDto = LabelViewDto("Hello, World!")
        When("toDomain is invoked") {
            val view = labelViewDto.toDomain()
            Then("it should be a LabelView") {
                view.shouldBeInstanceOf<LabelView>()
            }
            Then("it should have the correct text") {
                (view as LabelView).text shouldBe "Hello, World!"
            }
        }
    }

    Given("a ButtonViewDto") {
        val buttonViewDto = ButtonViewDto("Click me", EventDto("EVENT_TYPE"))
        When("toDomain is invoked") {
            val view = buttonViewDto.toDomain()
            Then("it should be a ButtonView") {
                view.shouldBeInstanceOf<ButtonView>()
            }
            Then("it should have the correct text") {
                (view as ButtonView).text shouldBe "Click me"
            }
            Then("it should have the correct onClick event") {
                (view as ButtonView).onClick.type shouldBe "EVENT_TYPE"
            }
        }
    }

    Given("a ColumnViewDto") {
        val columnViewDto = ColumnViewDto(
            children = listOf(
                LabelViewDto("I am a label"),
                ButtonViewDto("I am a button", EventDto("BUTTON_EVENT"))
            )
        )
        When("toDomain is invoked") {
            val view = columnViewDto.toDomain()
            Then("it should be a ColumnView") {
                view.shouldBeInstanceOf<ColumnView>()
            }
            Then("it should have the correct number of children") {
                (view as ColumnView).children.size shouldBe 2
            }
            Then("the first child should be a LabelView") {
                (view as ColumnView).children[0].shouldBeInstanceOf<LabelView>()
            }
            Then("the second child should be a ButtonView") {
                (view as ColumnView).children[1].shouldBeInstanceOf<ButtonView>()
            }
        }
    }

    Given("a UiStateDto") {
        val uiStateDto = UiStateDto(
            id = "testState",
            view = LabelViewDto("Test View")
        )
        When("toDomain is invoked") {
            val state = uiStateDto.toDomain()
            Then("it should be a UiState") {
                state.shouldBeInstanceOf<UiState>()
            }
            Then("it should have the correct id") {
                state.id shouldBe "testState"
            }
            Then("it should have the correct view") {
                (state as UiState).view.shouldBeInstanceOf<LabelView>()
                (state.view as LabelView).text shouldBe "Test View"
            }
        }
    }
})
