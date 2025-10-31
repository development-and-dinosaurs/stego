package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNode
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.GridUiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.GridUiNode

class GridUiNodeMapperTest :
    BehaviorSpec({
        Given("a GridUiNodeMapper") {
            val compositeUiNodeMapper =
                UiNodeMapper { dto ->
                    when (dto) {
                        is OtherUiNodeDto -> OtherUiNode
                        else -> throw IllegalArgumentException("Test DTO not recognised")
                    }
                }
            val mapper = GridUiNodeMapper(compositeUiNodeMapper)

            and("a GridUiNodeDto with children") {
                val dto =
                    GridUiNodeDto(
                        id = "grid-id",
                        columns = 4,
                        children = listOf(OtherUiNodeDto),
                    )

                When("the dto is mapped") {
                    val uiNode = mapper.map(dto)

                    Then("it should return a GridUiNode") {
                        uiNode.shouldBeInstanceOf<GridUiNode>()
                    }

                    Then("it should have the correct id") {
                        uiNode.id shouldBe "grid-id"
                    }

                    Then("it should have the correct number of columns") {
                        uiNode.columns shouldBe 4
                    }

                    Then("it should have correctly mapped children") {
                        uiNode.children shouldHaveSize 1
                        uiNode.children[0] shouldBe OtherUiNode
                    }
                }
            }

            and("a GridUiNodeDto with no children") {
                val dto = GridUiNodeDto(id = "empty-grid", columns = 2, children = emptyList())

                When("the dto is mapped") {
                    val uiNode = mapper.map(dto)

                    Then("it should have no children") {
                        uiNode.children.shouldBeEmpty()
                    }
                }
            }

            and("a non-GridUiNodeDto") {
                val dto = OtherUiNodeDto

                When("the dto is mapped") {
                    Then("it should throw an IllegalArgumentException") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                mapper.map(dto)
                            }
                        exception.message shouldBe "Failed requirement."
                    }
                }
            }
        }
    })
