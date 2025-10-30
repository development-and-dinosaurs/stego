package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.*
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto

class SerializationTest : BehaviorSpec({
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

    Given("a LabelUiNodeDto") {
        val labelUiNodeDto = LabelUiNodeDto("label-1", "I am a label")
        val expectedJson = """
            {
                "id": "label-1",
                "type": "label",
                "text": "I am a label"
            }
        """.trimIndent()

        When("it is serialized") {
            val serializedJson = json.encodeToString(labelUiNodeDto as UiNodeDto)
            Then("the JSON should be correct") {
                serializedJson shouldEqualJson expectedJson
            }
        }
    }

    Given("a ButtonUiNodeDto") {
        val buttonUiNodeDto = ButtonUiNodeDto(
            "button-1", "Click me",
            SubmitButtonActionDto(
                trigger = "SUBMIT_EVENT"
            )
        )
        val expectedJson = """
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

        When("it is serialized") {
            val serializedJson = json.encodeToString(buttonUiNodeDto as UiNodeDto)
            Then("the JSON should be correct") {
                serializedJson shouldEqualJson expectedJson
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
                RequiredValidationRuleDto("This field is required"),
                MinLengthValidationRuleDto("Must be at least 3 characters", 3)
            )
        )
        val expectedJson = """
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

        When("it is serialized") {
            val serializedJson = json.encodeToString(textFieldUiNodeDto as UiNodeDto)
            Then("the JSON should be correct") {
                serializedJson shouldEqualJson expectedJson
            }
        }
    }

    Given("a ProgressIndicatorUiNodeDto") {
        val progressIndicatorUiNodeDto = ProgressIndicatorUiNodeDto("progress-1")
        val expectedJson = """
            {
                "type": "progress_indicator",
                "id": "progress-1"
            }
        """.trimIndent()

        When("it is serialized") {
            val serializedJson = json.encodeToString(progressIndicatorUiNodeDto as UiNodeDto)
            Then("the JSON should be correct") {
                serializedJson shouldEqualJson expectedJson
            }
        }
    }

    Given("a ColumnUiNodeDto") {
        val columnUiNodeDto = ColumnUiNodeDto(
            "column-1",
            children = listOf(
                LabelUiNodeDto("label-in-column", "I am a label"),
                ButtonUiNodeDto(
                    "button-in-column", "Click me",
                    SubmitButtonActionDto(trigger = "BUTTON_EVENT")
                )
            )
        )
        val expectedJson = """
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

        When("it is serialized") {
            val serializedJson = json.encodeToString(columnUiNodeDto as UiNodeDto)
            Then("the JSON should be correct") {
                serializedJson shouldEqualJson expectedJson
            }
        }
    }

    Given("a UiStateDto") {
        val uiStateDto = UiStateDto(
            "testState",
            uiNode = LabelUiNodeDto("ui-state-label", "Test UiNode")
        )
        val expectedJson = """
            {
                "id": "testState",
                "uiNode": {
                    "type": "label",
                    "id": "ui-state-label",
                    "text": "Test UiNode"
                }
            }
        """.trimIndent()

        When("it is serialized") {
            val serializedJson = json.encodeToString(uiStateDto)
            Then("the JSON should be correct") {
                serializedJson shouldEqualJson expectedJson
            }
        }
    }
})