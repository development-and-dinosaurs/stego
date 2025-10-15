package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DeserializationTest : StringSpec({

    "Can deserialise a complex state machine definition from JSON" {
        val jsonString = """
        {
            "initial": "Idle",
            "initialContext": {
                "userId": {
                    "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LongPrimitiveDto",
                    "value": 123
                }
            },
            "states": {
                "Idle": {
                    "id": "Idle",
                    "on": {
                        "START": [
                            {
                                "target": "Active",
                                "guard": {
                                    "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.EqualsGuardDto",
                                    "left": {
                                        "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ContextReferenceDto",
                                        "path": "userId"
                                    },
                                    "right": {
                                        "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LiteralReferenceDto",
                                        "value": {
                                            "type": "uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LongPrimitiveDto",
                                            "value": 123
                                        }
                                    }
                                }
                            }
                        ]
                    }
                },
                "Active": {
                    "id": "Active"
                }
            }
        }
        """

        val json = Json { classDiscriminator = "type" }
        val definitionDto = json.decodeFromString<StateMachineDefinitionDto>(jsonString)

        // Assert on the Dto structure
        definitionDto.initial shouldBe "Idle"
        val initialContextValue = definitionDto.initialContext["userId"]
        initialContextValue.shouldBeInstanceOf<LongPrimitiveDto>()
        initialContextValue.value shouldBe 123L

        val idleStateDto = definitionDto.states["Idle"]
        val transitionDto = idleStateDto?.on?.get("START")?.first()
        transitionDto?.target shouldBe "Active"

        val guardDto = transitionDto?.guard
        guardDto.shouldBeInstanceOf<EqualsGuardDto>()
        guardDto.left.shouldBeInstanceOf<ContextReferenceDto>()
        guardDto.right.shouldBeInstanceOf<LiteralReferenceDto>()
        
        val literalValue = (guardDto.right).value
        literalValue.shouldBeInstanceOf<LongPrimitiveDto>()
        literalValue.value shouldBe 123L
    }
})
