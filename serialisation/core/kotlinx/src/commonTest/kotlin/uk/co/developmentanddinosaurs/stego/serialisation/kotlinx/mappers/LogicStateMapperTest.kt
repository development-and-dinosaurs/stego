package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.*
import uk.co.developmentanddinosaurs.stego.statemachine.Invokable
import uk.co.developmentanddinosaurs.stego.statemachine.LogicState

private data class OtherStateDto(
    override val id: String,
    override val initial: String? = null,
    override val invoke: InvokableDefinitionDto? = null,
    override val on: Map<String, List<TransitionDto>> = emptyMap(),
    override val onEntry: List<ActionDto> = emptyList(),
    override val onExit: List<ActionDto> = emptyList(),
    override val states: Map<String, StateDto> = emptyMap(),
) : StateDto

private object TestInvokable : Invokable {
    override suspend fun invoke(input: Map<String, Any?>) = throw NotImplementedError("Not used in this test")
}

class LogicStateMapperTest : BehaviorSpec({
    // Create mock/stub mappers for dependency injection
    val actionMapper = ActionMapper(
        customMappers = mapOf(
            TestActionDto::class to ActionDtoMapper { dto -> TestAction((dto as TestActionDto).data) },
        ),
    )
    val invokableRegistry = mapOf("testSrc" to TestInvokable)
    val invokableMapper = InvokableDefinitionMapper(invokableRegistry)
    val transitionMapper = TransitionMapper(actionMapper)

    // A simple state mapper for testing recursive calls
    val compositeStateMapper = StateDtoMapper { dto -> LogicState(id = "nested_${dto.id}") }

    Given("a LogicStateMapper") {
        val mapper = LogicStateMapper(actionMapper, invokableMapper, transitionMapper, compositeStateMapper)

        and("a simple LogicStateDto") {
            val dto = LogicStateDto(id = "Initial", initial = "Start")

            When("the dto is mapped") {
                val state = mapper.map(dto) as LogicState

                Then("it should map the basic properties correctly") {
                    state.id shouldBe "Initial"
                    state.initial shouldBe "Start"
                }
            }
        }

        and("a LogicStateDto with onEntry and onExit actions") {
            val dto =
                LogicStateDto(
                    id = "WithActions",
                    onEntry = listOf(TestActionDto("entry_action")),
                    onExit = listOf(TestActionDto("exit_action")),
                )

            When("the dto is mapped") {
                val state = mapper.map(dto) as LogicState

                Then("it should map the actions correctly") {
                    state.onEntry.shouldHaveSize(1)
                    state.onEntry.first().shouldBeInstanceOf<TestAction>()
                    (state.onEntry.first() as TestAction).data shouldBe "entry_action"

                    state.onExit.shouldHaveSize(1)
                    state.onExit.first().shouldBeInstanceOf<TestAction>()
                    (state.onExit.first() as TestAction).data shouldBe "exit_action"
                }
            }
        }

        and("a LogicStateDto with transitions") {
            val dto =
                LogicStateDto(
                    id = "WithTransitions",
                    on = mapOf("NEXT" to listOf(TransitionDto(target = "NextState"))),
                )

            When("the dto is mapped") {
                val state = mapper.map(dto) as LogicState

                Then("it should map the transitions correctly") {
                    state.on.shouldHaveSize(1)
                    state.on["NEXT"]?.first()?.target shouldBe "NextState"
                }
            }
        }

        and("a LogicStateDto with an invokable") {
            val dto =
                LogicStateDto(
                    id = "WithInvokable",
                    invoke = InvokableDefinitionDto(id = "testInvoke", src = "testSrc"),
                )

            When("the dto is mapped") {
                val state = mapper.map(dto) as LogicState

                Then("it should map the invokable definition correctly") {
                    state.invoke.shouldNotBeNull()
                    state.invoke?.id shouldBe "testInvoke"
                    state.invoke?.src shouldBe TestInvokable
                }
            }
        }

        and("a LogicStateDto with nested states") {
            val dto =
                LogicStateDto(
                    id = "WithNested",
                    states = mapOf("Nested" to LogicStateDto(id = "NestedState")),
                )

            When("the dto is mapped") {
                val state = mapper.map(dto) as LogicState

                Then("it should recursively map the nested states") {
                    state.states.shouldHaveSize(1)
                    state.states["Nested"]?.id shouldBe "nested_NestedState"
                }
            }
        }

        and("a non-LogicStateDto") {
            val dto = OtherStateDto(id = "other")

            When("the dto is mapped") {
                Then("it should throw an IllegalArgumentException") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            mapper.map(dto)
                        }
                    exception.message shouldBe "LogicStateMapper can only map LogicStateDto"
                }
            }
        }
    }
})
