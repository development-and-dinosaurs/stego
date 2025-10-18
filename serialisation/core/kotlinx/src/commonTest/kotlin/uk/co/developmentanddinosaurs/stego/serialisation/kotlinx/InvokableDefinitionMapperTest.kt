package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import uk.co.developmentanddinosaurs.stego.statemachine.*

class InvokableDefinitionMapperTest : BehaviorSpec({

    val dummyInvokable = Invokable { InvokableResult.Success() }
    val registry = mapOf("my-invoke" to dummyInvokable)
    val mapper = InvokableDefinitionMapper(registry)

    Given("an InvokableDefinitionDto with various inputs") {
        val dto =
            InvokableDefinitionDto(
                id = "testId",
                src = "my-invoke",
                input =
                buildJsonObject {
                    put("literalString", "hello")
                    put("literalLong", 123L)
                    put("literalDouble", 45.6)
                    put("literalBoolean", true)
                    put("contextRef", "context.user.id")
                    put("eventRef", "event.data.payload")
                },
            )

        When("the mapper maps the DTO") {
            val definition = mapper.map(dto)

            Then("it should return a correctly mapped InvokableDefinition") {
                definition.id shouldBe "testId"
                definition.src shouldBe dummyInvokable
                definition.input shouldContainExactly
                    mapOf(
                        "literalString" to StringPrimitive("hello"),
                        "literalLong" to LongPrimitive(123L),
                        "literalDouble" to DoublePrimitive(45.6),
                        "literalBoolean" to BooleanPrimitive(true),
                        "contextRef" to ContextReference("user.id"),
                        "eventRef" to EventReference("data.payload"),
                    )
            }
        }
    }

    Given("an InvokableDefinitionDto with no input") {
        val dto = InvokableDefinitionDto(id = "testId", src = "my-invoke", input = null)

        When("the mapper maps the DTO") {
            val definition = mapper.map(dto)

            Then("the resulting definition should have an empty input map") {
                definition.input.isEmpty() shouldBe true
            }
        }
    }

    Given("an InvokableDefinitionDto with an unknown src") {
        val dto = InvokableDefinitionDto(id = "testId", src = "unknown-invoke")

        When("the mapper maps the DTO") {
            Then("it should throw a StateMachineException") {
                val exception = shouldThrow<StateMachineException> { mapper.map(dto) }
                exception.message shouldBe "Invokable source 'unknown-invoke' not found in registry."
            }
        }
    }

    Given("an InvokableDefinitionDto with a null value in input") {
        val dto =
            InvokableDefinitionDto(
                id = "testId",
                src = "my-invoke",
                input = buildJsonObject { put("badValue", JsonNull) },
            )

        When("the mapper maps the DTO") {
            Then("it should throw a StateMachineException") {
                val exception = shouldThrow<StateMachineException> { mapper.map(dto) }
                exception.message shouldBe "Null values are not supported in invokable input."
            }
        }
    }

    Given("an InvokableDefinitionDto with a complex object in input") {
        val dto =
            InvokableDefinitionDto(
                id = "testId",
                src = "my-invoke",
                input =
                buildJsonObject {
                    put(
                        "complex",
                        buildJsonObject { put("a", "b") },
                    )
                },
            )

        When("the mapper maps the DTO") {
            Then("it should throw a StateMachineException") {
                val exception = shouldThrow<StateMachineException> { mapper.map(dto) }
                exception.message shouldBe "Complex objects in invokable input are not supported. Use expressions to reference context data."
            }
        }
    }
})