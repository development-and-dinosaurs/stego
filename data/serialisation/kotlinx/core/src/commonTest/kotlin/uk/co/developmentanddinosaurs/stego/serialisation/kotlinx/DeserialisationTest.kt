package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.developmentanddinosaurs.stego.serialisation.ActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.LogActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.LogicStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.serialisation.StateMachineDefinitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.BooleanDataValueDto
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.NullDataValueDto
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.NumberDataValueDto
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.StringDataValueDto

class DeserialisationTest :
    BehaviorSpec({

        Given("a JSON string representing a state machine definition") {
            val jsonString =
                """
                {
                  "initial": "start",
                  "initialContext": {
                    "a_string": "hello world",
                    "an_integer": 123,
                    "a_double": 45.67,
                    "a_boolean": true,
                    "a_null": null
                  },
                  "states": {
                    "start": {
                      "id": "start",
                      "type": "logic",
                      "on": {
                        "EVENT": [{
                          "target": "next",
                          "actions": [
                            {
                              "type": "log",
                              "message": "Transitioning from start to next"
                            }
                          ]
                        }]
                      }
                    },
                    "next": {
                      "id": "next",
                      "type": "logic"
                    }
                  }
                }
                """.trimIndent()

            When("the string is deserialised") {
                val json =
                    Json {
                        serializersModule =
                            SerializersModule {
                                polymorphic(StateDto::class) {
                                    subclass(LogicStateDto::class)
                                }
                                polymorphic(ActionDto::class) {
                                    subclass(LogActionDto::class)
                                }
                            }
                    }
                val dto = json.decodeFromString<StateMachineDefinitionDto>(jsonString)

                Then("it should correctly deserialise the top-level properties") {
                    dto.initial shouldBe "start"
                }

                Then("it should correctly deserialise the initial context") {
                    dto.initialContext shouldContainExactly
                        mapOf(
                            "a_string" to StringDataValueDto("hello world"),
                            "an_integer" to NumberDataValueDto(123L),
                            "a_double" to NumberDataValueDto(45.67),
                            "a_boolean" to BooleanDataValueDto(true),
                            "a_null" to NullDataValueDto,
                        )
                }

                Then("it should correctly deserialise the states and transitions") {
                    dto.states.keys shouldBe setOf("start", "next")
                    val startState = dto.states["start"]!!
                    val transitions = startState.on["EVENT"]!!
                    transitions[0].target shouldBe "next"
                }

                Then("it should correctly deserialise the actions within a transition") {
                    val startState = dto.states["start"]!!
                    val transitions = startState.on["EVENT"]!!
                    val action = transitions[0].actions.first()

                    action.shouldBeInstanceOf<LogActionDto>()
                    action.message shouldBe "Transitioning from start to next"
                }
            }
        }
    })
