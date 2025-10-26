package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

// A dummy encoder that is not a JsonEncoder, for testing failure paths.
private object NotJsonEncoder : Encoder {
    override val serializersModule: SerializersModule = EmptySerializersModule()
    override fun beginStructure(descriptor: SerialDescriptor) = TODO("Not yet implemented")
    override fun encodeBoolean(value: Boolean) = TODO("Not yet implemented")
    override fun encodeByte(value: Byte) = TODO("Not yet implemented")
    override fun encodeChar(value: Char) = TODO("Not yet implemented")
    override fun encodeDouble(value: Double) = TODO("Not yet implemented")
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = TODO("Not yet implemented")
    override fun encodeFloat(value: Float) = TODO("Not yet implemented")
    override fun encodeInline(descriptor: SerialDescriptor) = TODO("Not yet implemented")
    override fun encodeInt(value: Int) = TODO("Not yet implemented")
    override fun encodeLong(value: Long) = TODO("Not yet implemented")
    override fun encodeNull() = TODO("Not yet implemented")
    override fun encodeShort(value: Short) = TODO("Not yet implemented")
    override fun encodeString(value: String) = TODO("Not yet implemented")
}

// A dummy decoder that is not a JsonDecoder, for testing failure paths.
private object NotJsonDecoder : Decoder {
    override val serializersModule: SerializersModule = EmptySerializersModule()
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = TODO("Not yet implemented")
    override fun decodeBoolean(): Boolean = TODO("Not yet implemented")
    override fun decodeByte(): Byte = TODO("Not yet implemented")
    override fun decodeChar(): Char = TODO("Not yet implemented")
    override fun decodeDouble(): Double = TODO("Not yet implemented")
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = TODO("Not yet implemented")
    override fun decodeFloat(): Float = TODO("Not yet implemented")
    override fun decodeInline(descriptor: SerialDescriptor): Decoder = TODO("Not yet implemented")
    override fun decodeInt(): Int = TODO("Not yet implemented")
    override fun decodeLong(): Long = TODO("Not yet implemented")
    override fun decodeNotNullMark(): Boolean = TODO("Not yet implemented")
    override fun decodeNull(): Nothing = TODO("Not yet implemented")
    override fun decodeShort(): Short = TODO("Not yet implemented")
    override fun decodeString(): String = TODO("Not yet implemented")
}

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

        and("a non-JSON encoder") {
            val dto: DataValueDto = StringDataValueDto("hello")
            When("serialization is attempted") {
                Then("it should throw an IllegalStateException") {
                    val exception =
                        shouldThrow<IllegalStateException> {
                            DataValueDtoSerializer.serialize(NotJsonEncoder, dto)
                        }
                    exception.message shouldBe "This serializer can only be used with JSON"
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

        and("a non-JSON decoder") {
            When("deserialization is attempted") {
                Then("it should throw an IllegalStateException") {
                    val exception =
                        shouldThrow<IllegalStateException> { DataValueDtoSerializer.deserialize(NotJsonDecoder) }
                    exception.message shouldBe "This serializer can only be used with JSON"
                }
            }
        }
    }
})