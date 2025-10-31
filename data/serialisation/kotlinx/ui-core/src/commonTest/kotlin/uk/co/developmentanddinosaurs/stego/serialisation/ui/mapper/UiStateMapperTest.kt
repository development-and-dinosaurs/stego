package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.AssignActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.InvokableDefinitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.LogicStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.serialisation.TransitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.datavalue.StringDataValueDto
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.ActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.InvokableDefinitionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.StateDtoMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.TransitionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.LabelUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.statemachine.AssignAction
import uk.co.developmentanddinosaurs.stego.statemachine.Invokable
import uk.co.developmentanddinosaurs.stego.statemachine.InvokableResult
import uk.co.developmentanddinosaurs.stego.statemachine.LogicState
import uk.co.developmentanddinosaurs.stego.statemachine.State
import uk.co.developmentanddinosaurs.stego.statemachine.Transition
import uk.co.developmentanddinosaurs.stego.ui.UiState
import kotlin.reflect.KClass

private data class SimpleStateDto(
    override val id: String,
    override val initial: String? = null,
    override val invoke: InvokableDefinitionDto? = null,
    override val on: Map<String, List<TransitionDto>> = emptyMap(),
    override val onEntry: List<ActionDto> = emptyList(),
    override val onExit: List<ActionDto> = emptyList(),
    override val states: Map<String, StateDto> = emptyMap(),
) : StateDto

private class SimpleStateMapper : StateDtoMapper {
    override fun map(dto: StateDto): State = LogicState(dto.id)
}

class UiStateMapperTest :
    BehaviorSpec({
        Given("a UiStateMapper") {
            val stateMapper = SimpleStateMapper()
            val actionMapper = ActionMapper()
            val invokableMapper = InvokableDefinitionMapper(mapOf("invoke" to Invokable { InvokableResult.Success() }))
            val transitionMapper = TransitionMapper(actionMapper)
            val uiNodeMapper =
                CompositeUiNodeMapper(
                    simpleMappers =
                        mapOf<KClass<out UiNodeDto>, UiNodeMapper>(
                            LabelUiNodeDto::class to LabelUiNodeMapper(),
                        ),
                    compositeAwareFactories = emptyMap(),
                )

            val mapper = UiStateMapper(stateMapper, actionMapper, invokableMapper, transitionMapper, uiNodeMapper)

            and("a complete UiStateDto") {
                val dto =
                    UiStateDto(
                        id = "ui-state",
                        onEntry = listOf(AssignActionDto("onEntryKey", StringDataValueDto("onEntryValue"))),
                        onExit = listOf(AssignActionDto("onExitKey", StringDataValueDto("onExitValue"))),
                        on =
                            mapOf(
                                "EVENT" to
                                    listOf(
                                        TransitionDto(
                                            target = "some-target",
                                        ),
                                    ),
                            ),
                        invoke = InvokableDefinitionDto("invoke-id", "invoke"),
                        initial = "start",
                        states = mapOf("child" to SimpleStateDto("child")),
                        uiNode = LabelUiNodeDto("label-id", "label-text"),
                    )

                When("the dto is mapped") {
                    val state = mapper.map(dto)

                    Then("it should map all properties correctly") {
                        state.shouldBeInstanceOf<UiState>()
                        state.id shouldBe "ui-state"
                        state.initial shouldBe "start"
                        state.onEntry[0].shouldBeInstanceOf<AssignAction>()
                        state.onExit[0].shouldBeInstanceOf<AssignAction>()
                        state.on["EVENT"]?.get(0).shouldBeInstanceOf<Transition>()

                        state.states shouldHaveSize 1
                        state.states["child"] shouldBe LogicState("child")
                        state.uiNode.id shouldBe "label-id"
                    }
                }
            }

            and("a minimal UiStateDto") {
                val dto = UiStateDto(id = "minimal-state", uiNode = LabelUiNodeDto("a", "b"))

                When("the dto is mapped") {
                    val state = mapper.map(dto)

                    Then("it should map correctly with default empty values") {
                        state.shouldBeInstanceOf<UiState>()
                        state.id shouldBe "minimal-state"
                        state.uiNode.id shouldBe "a"
                    }
                }
            }

            and("a non-UiStateDto") {
                val dto = LogicStateDto(id = "non-ui-state")

                When("the dto is mapped") {
                    Then("it should throw an IllegalArgumentException") {
                        shouldThrow<IllegalArgumentException> {
                            mapper.map(dto)
                        }
                    }
                }
            }
        }
    })
