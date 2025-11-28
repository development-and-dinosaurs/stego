package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNode
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UnknownUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode
import kotlin.reflect.KClass

private data class CompositeUiNodeDto(
    val children: List<UiNodeDto>,
) : UiNodeDto {
    override val id: String = "composite"
}

private data class CompositeUiNode(
    val children: List<UiNode>,
) : UiNode {
    override val id: String = "composite"
}

class CompositeUiNodeMapperTest :
    BehaviorSpec({
        Given("a CompositeUiNodeMapper") {
            val simpleMappers =
                mapOf<KClass<out UiNodeDto>, UiNodeMapper>(
                    OtherUiNodeDto::class to UiNodeMapper { OtherUiNode },
                )

            val compositeAwareFactories =
                mapOf<KClass<out UiNodeDto>, (UiNodeMapper) -> UiNodeMapper>(
                    CompositeUiNodeDto::class to { compositeMapper ->
                        UiNodeMapper { dto ->
                            val compositeDto = dto as CompositeUiNodeDto
                            CompositeUiNode(compositeDto.children.map { compositeMapper.map(it) })
                        }
                    },
                )

            val mapper = CompositeUiNodeMapper(simpleMappers, compositeAwareFactories)

            and("a simple DTO") {
                val dto = OtherUiNodeDto

                When("the dto is mapped") {
                    val uiNode = mapper.map(dto)

                    Then("it should be mapped by the simple mapper") {
                        uiNode.shouldBeInstanceOf<OtherUiNode>()
                        uiNode.id shouldBe "child-id"
                    }
                }
            }

            and("a composite-aware DTO") {
                val dto = CompositeUiNodeDto(children = listOf(OtherUiNodeDto))

                When("the dto is mapped") {
                    val uiNode = mapper.map(dto)

                    Then("it should be mapped by the composite-aware mapper") {
                        uiNode.shouldBeInstanceOf<CompositeUiNode>()
                    }

                    Then("it should have recursively mapped its children") {
                        val compositeNode = uiNode as CompositeUiNode
                        compositeNode.children.size shouldBe 1
                        compositeNode.children[0].shouldBeInstanceOf<OtherUiNode>()
                    }
                }
            }

            and("an unknown DTO") {
                val dto = UnknownUiNodeDto

                When("the dto is mapped") {
                    Then("it should throw an IllegalArgumentException") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                mapper.map(dto)
                            }
                        exception.message shouldContain "Unsupported UiNodeDto type: UnknownUiNodeDto"
                    }
                }
            }
        }
    })
