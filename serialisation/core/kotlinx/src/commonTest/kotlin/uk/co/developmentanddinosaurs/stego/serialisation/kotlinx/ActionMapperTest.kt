package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.statemachine.*
import kotlin.reflect.KClass

private data class CustomActionDto(val data: String) : ActionDto

private data class CustomAction(val data: String) : Action {
    override fun execute(
        context: Context,
        event: Event,
    ): Context = context
}

class ActionMapperTest : BehaviorSpec({
    Given("an ActionMapper with an empty registry") {
        val mapper = ActionMapper(emptyMap())

        and("a built-in AssignActionDto") {
            val dto = AssignActionDto("key", StringDataValueDto("value"))

            When("the dto is mapped") {
                val action = mapper.map(dto)

                Then("it should return an AssignAction") {
                    action.shouldBeInstanceOf<AssignAction>()
                }
            }
        }

        and("a built-in LogActionDto") {
            val dto = LogActionDto("message")

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
                    val exception = shouldThrow<StateMachineException> { mapper.map(dto) }
                    exception.message shouldBe "Action DTO type 'CustomActionDto' not found in registry and is not a built-in type."
                }
            }
        }
    }

    Given("an ActionMapper with a custom action in the registry") {
        val registry = mapOf<KClass<out ActionDto>, (ActionDto) -> Action>(CustomActionDto::class to { dto: ActionDto -> CustomAction((dto as CustomActionDto).data) })
        val mapper = ActionMapper(registry)
        val dto = CustomActionDto("custom data")

        When("the custom dto is mapped") {
            val action = mapper.map(dto)

            Then("it should return the correct custom action from the registry") {
                action.shouldBeInstanceOf<CustomAction>()
                action.data shouldBe "custom data"
            }
        }
    }

    Given("an ActionMapper with a registry that shadows a built-in type") {
        val customAssignAction = CustomAction("shadowed")
        val registry =
            mapOf<KClass<out ActionDto>, (ActionDto) -> Action>(
                AssignActionDto::class to { customAssignAction },
            )
        val mapper = ActionMapper(registry)
        val dto = AssignActionDto("key", StringDataValueDto("value"))

        When("the built-in dto is mapped") {
            val action = mapper.map(dto)

            Then("it should return the built-in action, not the one from the registry") {
                action.shouldBeInstanceOf<AssignAction>()
            }
        }
    }
})
