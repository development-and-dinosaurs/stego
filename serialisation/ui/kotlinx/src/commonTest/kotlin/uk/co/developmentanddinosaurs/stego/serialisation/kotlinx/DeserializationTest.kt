package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.*
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto

class DeserializationTest : BehaviorSpec({
    val json = Json {
        prettyPrint = true
        encodeDefaults = false
        serializersModule = SerializersModule {
            polymorphic(UiNodeDto::class) {
                subclass(LabelUiNodeDto::class)
                subclass(ButtonUiNodeDto::class)
                subclass(ColumnUiNodeDto::class)
                subclass(TextFieldUiNodeDto::class)
                subclass(ProgressIndicatorUiNodeDto::class)
            }
            polymorphic(ButtonActionDto::class) {
                subclass(SubmitButtonActionDto::class)
                subclass(BypassValidationButtonActionDto::class)
            }
            polymorphic(ValidationRuleDto::class) {
                subclass(RequiredValidationRuleDto::class)
                subclass(MinLengthValidationRuleDto::class)
            }
        }
        classDiscriminator = "type"
    }

    Given("a JSON representation of a LabelUiNode") {
        val labelJson = """
            {
                "id": "label-1",
                "type": "label",
                "text": "I am a label"
            }
        """.trimIndent()

        When("it is deserialized") {
            val uiNodeDto = json.decodeFromString<UiNodeDto>(labelJson)

            Then("it should be a LabelUiNodeDto") {
                uiNodeDto.shouldBeInstanceOf<LabelUiNodeDto>()
            }
            Then("it should have the correct text") {
                (uiNodeDto as LabelUiNodeDto).text shouldBe "I am a label"
            }
            Then("it should have the correct id") {
                uiNodeDto.id shouldBe "label-1"
            }

            When("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiNodeDto)
                Then("the JSON should match the original") {
                    serializedJson shouldEqualJson labelJson
                }
            }
        }
    }

    Given("a JSON representation of a ButtonUiNode") {
        val buttonJson = """
            {
                "id": "button-1",
                "type": "button",
                "text": "Click me",
                "onClick": {
                    "type": "submit",
                    "trigger": "SUBMIT_EVENT"
                }
            }
        """.trimIndent()

        When("it is deserialized") {
            val uiNodeDto = json.decodeFromString<UiNodeDto>(buttonJson)

            Then("it should be a ButtonUiNodeDto") {
                uiNodeDto.shouldBeInstanceOf<ButtonUiNodeDto>()
            }
            Then("it should have the correct text") {
                (uiNodeDto as ButtonUiNodeDto).text shouldBe "Click me"
            }
            Then("it should have the correct onClick action") {
                val action = (uiNodeDto as ButtonUiNodeDto).onClick
                action.shouldBeInstanceOf<SubmitButtonActionDto>()
                action.trigger shouldBe "SUBMIT_EVENT"
            }

            When("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiNodeDto)
                Then("the JSON should match the original") {
                    serializedJson shouldEqualJson buttonJson
                }
            }
        }
    }

    Given("a JSON representation of a TextFieldUiNode") {
        val textFieldJson = """
            {
                "type": "text_field",
                "id": "text-field-1",
                "text": "initial text",
                "label": "label",
                "onTextChanged": {
                    "trigger": "TEXT_CHANGED"
                },
                "validators": [
                    {
                        "type": "required",
                        "message": "This field is required"
                    },
                    {
                        "type": "minLength",
                        "message": "Must be at least 3 characters",
                        "length": 3
                    }
                ]
            }
        """.trimIndent()

        When("it is deserialized") {
            val uiNodeDto = json.decodeFromString<UiNodeDto>(textFieldJson)

            Then("it should be a TextFieldUiNodeDto") {
                uiNodeDto.shouldBeInstanceOf<TextFieldUiNodeDto>()
            }
            Then("it should have the correct text") {
                (uiNodeDto as TextFieldUiNodeDto).text shouldBe "initial text"
            }
            Then("it should have the correct label") {
                (uiNodeDto as TextFieldUiNodeDto).label shouldBe "label"
            }
            Then("it should have the correct onTextChanged interaction") {
                (uiNodeDto as TextFieldUiNodeDto).onTextChanged.trigger shouldBe "TEXT_CHANGED"
            }
            Then("it should have the correct validators") {
                val validators = (uiNodeDto as TextFieldUiNodeDto).validators
                validators.size shouldBe 2
                validators[0].shouldBeInstanceOf<RequiredValidationRuleDto>()
                val minLengthValidator = validators[1].shouldBeInstanceOf<MinLengthValidationRuleDto>()
                minLengthValidator.length shouldBe 3
                minLengthValidator.message shouldBe "Must be at least 3 characters"
            }

            When("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiNodeDto)
                Then("the JSON should match the original") {
                    serializedJson shouldEqualJson textFieldJson
                }
            }
        }
    }

    Given("a JSON representation of a ProgressIndicatorUiNode") {
        val progressIndicatorJson = """
            {
                "type": "progress_indicator",
                "id": "progress-1"
            }
        """.trimIndent()

        When("it is deserialized") {
            val uiNodeDto = json.decodeFromString<UiNodeDto>(progressIndicatorJson)

            Then("it should be a ProgressIndicatorUiNodeDto") {
                uiNodeDto.shouldBeInstanceOf<ProgressIndicatorUiNodeDto>()
            }

            When("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiNodeDto)
                Then("the JSON should match the original") {
                    serializedJson shouldEqualJson progressIndicatorJson
                }
            }
        }
    }

    Given("a JSON representation of a ColumnUiNode") {
        val columnJson = """
            {
                "type": "column",
                "id": "column-1",
                "children": [
                    {
                        "type": "label",
                        "id": "label-in-column",
                        "text": "I am a label"
                    },
                    {
                        "type": "button",
                        "id": "button-in-column",
                        "text": "Click me",
                        "onClick": {
                            "type": "submit",
                            "trigger": "BUTTON_EVENT"
                        }
                    }
                ]
            }
        """.trimIndent()

        When("it is deserialized") {
            val uiNodeDto = json.decodeFromString<UiNodeDto>(columnJson)

            Then("it should be a ColumnUiNodeDto") {
                uiNodeDto.shouldBeInstanceOf<ColumnUiNodeDto>()
            }
            Then("it should have the correct number of children") {
                (uiNodeDto as ColumnUiNodeDto).children.size shouldBe 2
            }
            Then("the first child should be a LabelUiNodeDto") {
                (uiNodeDto as ColumnUiNodeDto).children[0].shouldBeInstanceOf<LabelUiNodeDto>()
            }
            Then("the second child should be a ButtonUiNodeDto") {
                (uiNodeDto as ColumnUiNodeDto).children[1].shouldBeInstanceOf<ButtonUiNodeDto>()
            }

            When("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiNodeDto)
                Then("the JSON should match the original") {
                    serializedJson shouldEqualJson columnJson
                }
            }
        }
    }

    Given("a JSON representation of a UiState") {
        val uiStateJson = """
            {
                "id": "testState",
                "uiNode": {
                    "type": "label",
                    "id": "ui-state-label",
                    "text": "Test UiNode"
                }
            }
        """.trimIndent()

        When("it is deserialized") {
            val uiStateDto = json.decodeFromString<UiStateDto>(uiStateJson)

            Then("it should have the correct id") {
                uiStateDto.id shouldBe "testState"
            }
            Then("it should have the correct view") {
                uiStateDto.uiNode.shouldBeInstanceOf<LabelUiNodeDto>()
                uiStateDto.uiNode.text shouldBe "Test UiNode"
            }

            When("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiStateDto)
                Then("the JSON should match the original") {
                    serializedJson shouldEqualJson uiStateJson
                }
            }
        }
    }
})
