package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.datavalue.StringDataValueDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers.ActionDtoMapper
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers.ActionMapper
import uk.co.developmentanddinosaurs.stego.statemachine.*
import kotlin.reflect.KClass

private data class CustomActionDto(val data: String) :
    uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ActionDto

private data class CustomAction(val data: String) : Action {
    override fun execute(
        context: Context,
        event: Event,
    ): Context = context
}

private class OverridingAssignActionMapper : ActionDtoMapper {
    override fun map(dto: uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ActionDto): Action = CustomAction("overridden")
}

class ActionMapperTest : BehaviorSpec({
    Given("an ActionMapper with default mappers") {
        val mapper = ActionMapper()

        and("a built-in AssignActionDto") {
            val dto = _root_ide_package_.uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.AssignActionDto(
                "key",
                StringDataValueDto("value")
            )

            When("the dto is mapped") {
                val action = mapper.map(dto)

                Then("it should return an AssignAction") {
                    action.shouldBeInstanceOf<AssignAction>()
                }
            }
        }

        and("a built-in LogActionDto") {
            val dto =
                _root_ide_package_.uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LogActionDto("message")

            When("the dto is mapped") {
                val action = mapper.map(dto)

                Then("it should return a LogAction") {
                    action.shouldBeInstanceOf<LogAction>()
                }
            }
        }

        and("an unknown ActionDto") {
            val dto = CustomActionDto("some data")

            When("the dto is mapped") {
                Then("it should throw a StateMachineException") {
                    val exception =
                        shouldThrow<StateMachineException> { mapper.map(dto) }
                    exception.message shouldBe "Action DTO type 'CustomActionDto' not found in mapper registry."
                }
            }
        }
    }

    Given("an ActionMapper with a custom action mapper provided via a lambda") {
        val customMappers =
            mapOf<KClass<out uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ActionDto>, ActionDtoMapper>(
                CustomActionDto::class to ActionDtoMapper { dto -> CustomAction((dto as CustomActionDto).data) },
            )
        val mapper = ActionMapper(customMappers)
        val dto = CustomActionDto("custom data")

        When("the custom dto is mapped") {
            val action = mapper.map(dto)

            Then("it should return the correct custom action from the registry") {
                action.shouldBeInstanceOf<CustomAction>()
                action.data shouldBe "custom data"
            }
        }
    }

    Given("an ActionMapper that overrides a default mapper") {
        val customMappers =
            mapOf<KClass<out uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ActionDto>, ActionDtoMapper>(
                _root_ide_package_.uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.AssignActionDto::class to OverridingAssignActionMapper(),
            )
        val mapper = ActionMapper(customMappers)
        val dto = _root_ide_package_.uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.AssignActionDto(
            "key",
            StringDataValueDto("value")
        )

        When("the built-in dto is mapped") {
            val action = mapper.map(dto)

            Then("it should return the overridden action") {
                action.shouldBeInstanceOf<CustomAction>()
                action.data shouldBe "overridden"
            }
        }
    }
})
