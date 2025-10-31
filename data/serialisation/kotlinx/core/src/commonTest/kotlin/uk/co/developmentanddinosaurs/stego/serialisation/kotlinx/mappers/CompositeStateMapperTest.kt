package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.*
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.NullDataValueDto
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.CompositeStateMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.StateDtoMapper
import uk.co.developmentanddinosaurs.stego.statemachine.LogicState
import uk.co.developmentanddinosaurs.stego.statemachine.State
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException
import kotlin.reflect.KClass

// Test-specific DTOs and Mappers
private data class SimpleStateDto(
    override val id: String,
    override val initial: String? = null,
    override val invoke: InvokableDefinitionDto? = null,
    override val on: Map<String, List<TransitionDto>> = emptyMap(),
    override val onEntry: List<ActionDto> = emptyList(),
    override val onExit: List<ActionDto> = emptyList(),
    override val states: Map<String, StateDto> = emptyMap(),
) : StateDto

private data class HierarchicalStateDto(
    override val id: String,
    override val states: Map<String, StateDto> = emptyMap(),
    override val initial: String? = null,
    override val invoke: InvokableDefinitionDto? = null,
    override val on: Map<String, List<TransitionDto>> = emptyMap(),
    override val onEntry: List<ActionDto> = emptyList(),
    override val onExit: List<ActionDto> = emptyList(),
) : StateDto

private data class UnknownStateDto(
    override val id: String,
    override val initial: String? = null,
    override val invoke: InvokableDefinitionDto? = null,
    override val on: Map<String, List<TransitionDto>> = emptyMap(),
    override val onEntry: List<ActionDto> = emptyList(),
    override val onExit: List<ActionDto> = emptyList(),
    override val states: Map<String, StateDto> = emptyMap(),
) : StateDto

private class SimpleStateMapper : StateDtoMapper {
    override fun map(dto: StateDto): State {
        require(dto is SimpleStateDto)
        return LogicState(dto.id)
    }
}

private class HierarchicalStateMapper(
    private val compositeStateMapper: StateDtoMapper,
) : StateDtoMapper {
    override fun map(dto: StateDto): State {
        require(dto is HierarchicalStateDto)
        return LogicState(
            id = dto.id,
            states = dto.states.mapValues { (_, stateDto) -> compositeStateMapper.map(stateDto) },
        )
    }
}

class CompositeStateMapperTest : BehaviorSpec({
    val mapperFactories =
        mapOf<KClass<out StateDto>, (StateDtoMapper) -> StateDtoMapper>(
            SimpleStateDto::class to { _ -> SimpleStateMapper() },
            HierarchicalStateDto::class to { compositeMapper -> HierarchicalStateMapper(compositeMapper) },
        )

    Given("a CompositeStateMapper with registered mappers") {
        val mapper = CompositeStateMapper(mapperFactories)

        and("a simple state machine definition") {
            val dto =
                StateMachineDefinitionDto(
                    initial = "Initial",
                    states = mapOf("Initial" to SimpleStateDto("Initial")),
                )

            When("the definition is mapped") {
                val definition = mapper.map(dto)

                Then("it should produce a valid StateMachineDefinition") {
                    definition.initial shouldBe "Initial"
                    definition.states.shouldHaveSize(1)
                    definition.states["Initial"].shouldBeInstanceOf<LogicState>()
                }
            }
        }

        and("a hierarchical state machine definition") {
            val dto =
                StateMachineDefinitionDto(
                    initial = "Parent",
                    states =
                        mapOf(
                            "Parent" to
                                    HierarchicalStateDto(
                                        id = "Parent",
                                        states = mapOf("Child" to SimpleStateDto("Child")),
                                    ),
                        ),
                )

            When("the definition is mapped") {
                val definition = mapper.map(dto)

                Then("it should correctly map parent and child states") {
                    val parent = definition.states["Parent"] as LogicState
                    parent.states.shouldHaveSize(1)
                    parent.states["Child"].shouldBeInstanceOf<LogicState>()
                }
            }
        }

        and("a definition with an unknown state type") {
            val dto =
                StateMachineDefinitionDto(
                    initial = "Initial",
                    states = mapOf("Initial" to UnknownStateDto("Initial")),
                )

            When("the definition is mapped") {
                Then("it should throw a StateMachineException") {
                    val exception = shouldThrow<StateMachineException> { mapper.map(dto) }
                    exception.message shouldBe "Unsupported StateDto type: UnknownStateDto"
                }
            }
        }

        and("a definition with a null initial context value") {
            val dto =
                StateMachineDefinitionDto(
                    initial = "Initial",
                    states = mapOf("Initial" to SimpleStateDto("Initial")),
                    initialContext = mapOf("badValue" to NullDataValueDto),
                )

            When("the definition is mapped") {
                Then("it should throw a StateMachineException") {
                    val exception =
                        shouldThrow<StateMachineException> { mapper.map(dto) }
                    exception.message shouldBe "Failed to map initial context value: NullDataValueDto"
                }
            }
        }
    }
})