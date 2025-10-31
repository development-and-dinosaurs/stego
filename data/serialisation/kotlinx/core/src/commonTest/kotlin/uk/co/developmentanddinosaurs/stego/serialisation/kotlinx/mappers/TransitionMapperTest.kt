package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.TransitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.TestAction
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.TestActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.ActionDtoMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.TransitionMapper
import uk.co.developmentanddinosaurs.stego.statemachine.guards.EqualsGuard

class TransitionMapperTest :
    BehaviorSpec({
        // A simple action mapper for testing purposes.
        val actionMapper =
            ActionDtoMapper { dto ->
                when (dto) {
                    is TestActionDto -> TestAction(dto.data)
                    else -> throw Exception("Test action mapper received an unknown DTO")
                }
            }

        Given("a TransitionMapper") {
            val mapper = TransitionMapper(actionMapper)

            and("a transition DTO with only a target") {
                val dto = TransitionDto(target = "NextState")

                When("the dto is mapped") {
                    val transition = mapper.map(dto)

                    Then("it should have the correct target and empty actions and a null guard") {
                        transition.target shouldBe "NextState"
                        transition.actions.shouldBeEmpty()
                        transition.guard.shouldBeNull()
                    }
                }
            }

            and("a transition DTO with actions") {
                val dto =
                    TransitionDto(
                        target = "NextState",
                        actions = listOf(TestActionDto("action_data")),
                    )

                When("the dto is mapped") {
                    val transition = mapper.map(dto)

                    Then("it should map the actions correctly") {
                        transition.actions.shouldHaveSize(1)
                        val action = transition.actions.first()
                        action.shouldBeInstanceOf<TestAction>()
                        action.data shouldBe "action_data"
                    }
                }
            }

            and("a transition DTO with a guard") {
                val dto =
                    TransitionDto(
                        target = "NextState",
                        guard = "(a == b)",
                    )

                When("the dto is mapped") {
                    val transition = mapper.map(dto)

                    Then("it should parse the guard correctly") {
                        transition.guard.shouldNotBeNull()
                        transition.guard.shouldBeInstanceOf<EqualsGuard>()
                    }
                }
            }
        }
    })
