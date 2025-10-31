package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.*

// A dummy encoder that is not a JsonEncoder, for testing failure paths.
@ExperimentalSerializationApi
private object NotJsonEncoder : Encoder {
    override val serializersModule: SerializersModule = EmptySerializersModule()
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        throw UnsupportedOperationException("Not used in this test")

    override fun encodeBoolean(value: Boolean) {}
    override fun encodeByte(value: Byte) {}
    override fun encodeChar(value: Char) {}
    override fun encodeDouble(value: Double) {}
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {}
    override fun encodeFloat(value: Float) {}
    override fun encodeInline(descriptor: SerialDescriptor): Encoder = this
    override fun encodeInt(value: Int) {}
    override fun encodeLong(value: Long) {}
    override fun encodeNull() {}
    override fun encodeShort(value: Short) {}
    override fun encodeString(value: String) {}
}

// A dummy decoder that is not a JsonDecoder, for testing failure paths.
@ExperimentalSerializationApi
private object NotJsonDecoder : Decoder {
    override val serializersModule: SerializersModule = EmptySerializersModule()
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
        throw UnsupportedOperationException("Not used in this test")

    override fun decodeBoolean(): Boolean = false
    override fun decodeByte(): Byte = 0
    override fun decodeChar(): Char = ' '
    override fun decodeDouble(): Double = 0.0
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = 0
    override fun decodeFloat(): Float = 0.0f
    override fun decodeInline(descriptor: SerialDescriptor): Decoder = this
    override fun decodeInt(): Int = 0
    override fun decodeLong(): Long = 0L
    override fun decodeNotNullMark(): Boolean = false
    override fun decodeNull(): Nothing? = null
    override fun decodeShort(): Short = 0
    override fun decodeString(): String = ""
}

@OptIn(ExperimentalSerializationApi::class)
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