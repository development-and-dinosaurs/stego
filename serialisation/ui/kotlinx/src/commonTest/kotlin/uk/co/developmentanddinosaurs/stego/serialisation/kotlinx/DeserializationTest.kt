package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.*

class DeserializationTest : BehaviorSpec({
    val json = Json {
        prettyPrint = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            polymorphic(UiNodeDto::class) {
                subclass(LabelUiNodeDto::class)
                subclass(ButtonUiNodeDto::class)
                subclass(ColumnUiNodeDto::class)
                subclass(TextFieldUiNodeDto::class)
                subclass(ProgressIndicatorUiNodeDto::class)
            }
        }
        classDiscriminator = "type"
    }

    given("a JSON representation of a LabelUiNode") {
        val labelJson = """
            {
                "type": "label",
                "text": "I am a label"
            }
        """.trimIndent()

        `when`("it is deserialized") {
            val uiNodeDto = json.decodeFromString<UiNodeDto>(labelJson)

            then("it should be a LabelUiNodeDto") {
                uiNodeDto.shouldBeInstanceOf<LabelUiNodeDto>()
            }
            then("it should have the correct text") {
                (uiNodeDto as LabelUiNodeDto).text shouldBe "I am a label"
            }

            `when`("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiNodeDto)
                then("the JSON should match the original") {
                    serializedJson shouldBe labelJson
                }
            }
        }
    }

    given("a JSON representation of a ButtonUiNode") {
        val buttonJson = """
            {
                "type": "button",
                "text": "Click me",
                "onClick": {
                    "type": "BUTTON_EVENT",
                    "data": {}
                }
            }
        """.trimIndent()

        `when`("it is deserialized") {
            val uiNodeDto = json.decodeFromString<UiNodeDto>(buttonJson)

            then("it should be a ButtonUiNodeDto") {
                uiNodeDto.shouldBeInstanceOf<ButtonUiNodeDto>()
            }
            then("it should have the correct text") {
                (uiNodeDto as ButtonUiNodeDto).text shouldBe "Click me"
            }
            then("it should have the correct onClick event") {
                (uiNodeDto as ButtonUiNodeDto).onClick.type shouldBe "BUTTON_EVENT"
            }

            `when`("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiNodeDto)
                then("the JSON should match the original") {
                    serializedJson shouldBe buttonJson
                }
            }
        }
    }

    given("a JSON representation of a TextFieldUiNode") {
        val textFieldJson = """
            {
                "type": "text_field",
                "text": "initial text",
                "label": "label",
                "onTextChanged": {
                    "type": "TEXT_CHANGED",
                    "data": {}
                }
            }
        """.trimIndent()

        `when`("it is deserialized") {
            val uiNodeDto = json.decodeFromString<UiNodeDto>(textFieldJson)

            then("it should be a TextFieldUiNodeDto") {
                uiNodeDto.shouldBeInstanceOf<TextFieldUiNodeDto>()
            }
            then("it should have the correct text") {
                (uiNodeDto as TextFieldUiNodeDto).text shouldBe "initial text"
            }
            then("it should have the correct label") {
                (uiNodeDto as TextFieldUiNodeDto).label shouldBe "label"
            }
            then("it should have the correct onTextChanged event") {
                (uiNodeDto as TextFieldUiNodeDto).onTextChanged.type shouldBe "TEXT_CHANGED"
            }

            `when`("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiNodeDto)
                then("the JSON should match the original") {
                    serializedJson shouldBe textFieldJson
                }
            }
        }
    }

    given("a JSON representation of a ProgressIndicatorUiNode") {
        val progressIndicatorJson = """
            {
                "type": "progress_indicator"
            }
        """.trimIndent()

        `when`("it is deserialized") {
            val uiNodeDto = json.decodeFromString<UiNodeDto>(progressIndicatorJson)

            then("it should be a ProgressIndicatorUiNodeDto") {
                uiNodeDto.shouldBeInstanceOf<ProgressIndicatorUiNodeDto>()
            }

            `when`("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiNodeDto)
                then("the JSON should match the original") {
                    serializedJson shouldBe progressIndicatorJson
                }
            }
        }
    }

    given("a JSON representation of a ColumnUiNode") {
        val columnJson = """
            {
                "type": "column",
                "children": [
                    {
                        "type": "label",
                        "text": "I am a label"
                    },
                    {
                        "type": "button",
                        "text": "Click me",
                        "onClick": {
                            "type": "BUTTON_EVENT",
                            "data": {}
                        }
                    }
                ]
            }
        """.trimIndent()

        `when`("it is deserialized") {
            val uiNodeDto = json.decodeFromString<UiNodeDto>(columnJson)

            then("it should be a ColumnUiNodeDto") {
                uiNodeDto.shouldBeInstanceOf<ColumnUiNodeDto>()
            }
            then("it should have the correct number of children") {
                (uiNodeDto as ColumnUiNodeDto).children.size shouldBe 2
            }
            then("the first child should be a LabelUiNodeDto") {
                (uiNodeDto as ColumnUiNodeDto).children[0].shouldBeInstanceOf<LabelUiNodeDto>()
            }
            then("the second child should be a ButtonUiNodeDto") {
                (uiNodeDto as ColumnUiNodeDto).children[1].shouldBeInstanceOf<ButtonUiNodeDto>()
            }

            `when`("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiNodeDto)
                then("the JSON should match the original") {
                    serializedJson shouldBe columnJson
                }
            }
        }
    }

    given("a JSON representation of a UiState") {
        val uiStateJson = """
            {
                "id": "testState",
                "initial": null,
                "invoke": null,
                "on": {},
                "onEntry": [],
                "onExit": [],
                "states": {},
                "uiNode": {
                    "type": "label",
                    "text": "Test UiNode"
                }
            }
        """.trimIndent()

        `when`("it is deserialized") {
            val uiStateDto = json.decodeFromString<UiStateDto>(uiStateJson)

            then("it should have the correct id") {
                uiStateDto.id shouldBe "testState"
            }
            then("it should have the correct view") {
                uiStateDto.uiNode.shouldBeInstanceOf<LabelUiNodeDto>()
                uiStateDto.uiNode.text shouldBe "Test UiNode"
            }

            `when`("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(uiStateDto)
                then("the JSON should match the original") {
                    serializedJson shouldBe uiStateJson
                }
            }
        }
    }
})
