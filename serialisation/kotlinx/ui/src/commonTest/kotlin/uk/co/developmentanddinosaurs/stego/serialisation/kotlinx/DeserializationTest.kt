package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

class DeserializationTest : BehaviorSpec({
    val json = Json {
        prettyPrint = true
        serializersModule = SerializersModule {
            polymorphic(ViewDto::class) {
                subclass(LabelViewDto::class)
                subclass(ButtonViewDto::class)
                subclass(ColumnViewDto::class)
            }
        }
        classDiscriminator = "type"
    }

    given("a JSON representation of a LabelView") {
        val labelJson = """
            {
                "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LabelViewDto",
                "text": "I am a label"
            }
        """.trimIndent()

        `when`("it is deserialized") {
            val viewDto = json.decodeFromString<ViewDto>(labelJson)

            then("it should be a LabelViewDto") {
                viewDto.shouldBeInstanceOf<LabelViewDto>()
            }
            then("it should have the correct text") {
                (viewDto as LabelViewDto).text shouldBe "I am a label"
            }

            `when`("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(viewDto)
                then("the JSON should match the original") {
                    serializedJson shouldBe labelJson
                }
            }
        }
    }

    given("a JSON representation of a ButtonView") {
        val buttonJson = """
            {
                "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ButtonViewDto",
                "text": "Click me",
                "onClick": {
                    "type": "BUTTON_EVENT"
                }
            }
        """.trimIndent()

        `when`("it is deserialized") {
            val viewDto = json.decodeFromString<ViewDto>(buttonJson)

            then("it should be a ButtonViewDto") {
                viewDto.shouldBeInstanceOf<ButtonViewDto>()
            }
            then("it should have the correct text") {
                (viewDto as ButtonViewDto).text shouldBe "Click me"
            }
            then("it should have the correct onClick event") {
                (viewDto as ButtonViewDto).onClick.type shouldBe "BUTTON_EVENT"
            }

            `when`("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(viewDto)
                then("the JSON should match the original") {
                    serializedJson shouldBe buttonJson
                }
            }
        }
    }

    given("a JSON representation of a ColumnView") {
        val columnJson = """
            {
                "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ColumnViewDto",
                "children": [
                    {
                        "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LabelViewDto",
                        "text": "I am a label"
                    },
                    {
                        "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ButtonViewDto",
                        "text": "Click me",
                        "onClick": {
                            "type": "BUTTON_EVENT"
                        }
                    }
                ]
            }
        """.trimIndent()

        `when`("it is deserialized") {
            val viewDto = json.decodeFromString<ViewDto>(columnJson)

            then("it should be a ColumnViewDto") {
                viewDto.shouldBeInstanceOf<ColumnViewDto>()
            }
            then("it should have the correct number of children") {
                (viewDto as ColumnViewDto).children.size shouldBe 2
            }
            then("the first child should be a LabelViewDto") {
                (viewDto as ColumnViewDto).children[0].shouldBeInstanceOf<LabelViewDto>()
            }
            then("the second child should be a ButtonViewDto") {
                (viewDto as ColumnViewDto).children[1].shouldBeInstanceOf<ButtonViewDto>()
            }

            `when`("it is serialized back to JSON") {
                val serializedJson = json.encodeToString(viewDto)
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
                "onEntry": [],
                "onExit": [],
                "on": {},
                "invoke": null,
                "initial": null,
                "states": {},
                "view": {
                    "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LabelViewDto",
                    "text": "Test View"
                }
            }
        """.trimIndent()

        `when`("it is deserialized") {
            val uiStateDto = json.decodeFromString<UiStateDto>(uiStateJson)

            then("it should have the correct id") {
                uiStateDto.id shouldBe "testState"
            }
            then("it should have the correct view") {
                uiStateDto.view.shouldBeInstanceOf<LabelViewDto>()
                uiStateDto.view.text shouldBe "Test View"
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
