package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.*

class DataValueDtoSerializerTest : BehaviorSpec({
    Given("a DataValueDtoSerializer for serialization") {
        and("a StringDataValueDto") {
            val dto: DataValueDto = StringDataValueDto("hello")
            When("it is serialized") {
                val jsonElement = Json.encodeToJsonElement(DataValueDtoSerializer, dto)
                Then("it should produce a JSON string primitive") {
                    jsonElement.shouldBeInstanceOf<JsonPrimitive>()
                    jsonElement.isString shouldBe true
                    jsonElement.content shouldBe "hello"
                }
            }
        }

        and("a NumberDataValueDto (Long)") {
            val dto: DataValueDto = NumberDataValueDto(123L)
            When("it is serialized") {
                val jsonElement = Json.encodeToJsonElement(DataValueDtoSerializer, dto)
                Then("it should produce a JSON number primitive") {
                    jsonElement.shouldBeInstanceOf<JsonPrimitive>()
                    jsonElement.longOrNull shouldBe 123L
                }
            }
        }

        and("a BooleanDataValueDto") {
            val dto: DataValueDto = BooleanDataValueDto(true)
            When("it is serialized") {
                val jsonElement = Json.encodeToJsonElement(DataValueDtoSerializer, dto)
                Then("it should produce a JSON boolean primitive") {
                    jsonElement.shouldBeInstanceOf<JsonPrimitive>()
                    jsonElement.booleanOrNull shouldBe true
                }
            }
        }

        and("a NullDataValueDto") {
            val dto: DataValueDto = NullDataValueDto
            When("it is serialized") {
                val jsonElement = Json.encodeToJsonElement(DataValueDtoSerializer, dto)
                Then("it should produce a JSON null") {
                    jsonElement shouldBe JsonNull
                }
            }
        }
    }

    Given("a DataValueDtoSerializer for deserialization") {
        and("a JSON string primitive") {
            val jsonElement = JsonPrimitive("world")
            When("it is deserialized") {
                val dto = Json.decodeFromJsonElement(DataValueDtoSerializer, jsonElement)
                Then("it should produce a StringDataValueDto") {
                    dto.shouldBeInstanceOf<StringDataValueDto>()
                    dto.value shouldBe "world"
                }
            }
        }

        and("a JSON integer primitive") {
            val jsonElement = JsonPrimitive(456)
            When("it is deserialized") {
                val dto = Json.decodeFromJsonElement(DataValueDtoSerializer, jsonElement)
                Then("it should produce a NumberDataValueDto") {
                    dto.shouldBeInstanceOf<NumberDataValueDto>()
                    dto.value shouldBe 456L
                }
            }
        }

        and("a JSON boolean primitive") {
            val jsonElement = JsonPrimitive(false)
            When("it is deserialized") {
                val dto = Json.decodeFromJsonElement(DataValueDtoSerializer, jsonElement)
                Then("it should produce a BooleanDataValueDto") {
                    dto.shouldBeInstanceOf<BooleanDataValueDto>()
                    dto.value shouldBe false
                }
            }
        }

        and("a JSON null") {
            val jsonElement = JsonNull
            When("it is deserialized") {
                val dto = Json.decodeFromJsonElement(DataValueDtoSerializer, jsonElement)
                Then("it should produce a NullDataValueDto") {
                    dto shouldBe NullDataValueDto
                }
            }
        }

        and("a JSON object") {
            val jsonElement = buildJsonObject { put("key", "value") }
            When("it is deserialized") {
                Then("it should throw an IllegalStateException") {
                    val exception =
                        shouldThrow<IllegalStateException> {
                            Json.decodeFromJsonElement(DataValueDtoSerializer, jsonElement)
                        }
                    exception.message shouldBe "DataValueDto can only be deserialized from a JSON primitive or null"
                }
            }
        }

        and("a JSON array") {
            val jsonElement = buildJsonArray { add(JsonPrimitive(1)) }
            When("it is deserialized") {
                Then("it should throw an IllegalStateException") {
                    val exception =
                        shouldThrow<IllegalStateException> {
                            Json.decodeFromJsonElement(DataValueDtoSerializer, jsonElement)
                        }
                    exception.message shouldBe "DataValueDto can only be deserialized from a JSON primitive or null"
                }
            }
        }
    }
})