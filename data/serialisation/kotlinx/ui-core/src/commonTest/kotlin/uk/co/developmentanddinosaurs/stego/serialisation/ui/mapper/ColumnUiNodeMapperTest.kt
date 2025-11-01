package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNode
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ColumnUiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.ColumnUiNode

class ColumnUiNodeMapperTest : BehaviorSpec({
    Given("a ColumnUiNodeMapper") {
        val compositeUiNodeMapper =
            UiNodeMapper { dto ->
                when (dto) {
                    is OtherUiNodeDto -> OtherUiNode
                    else -> throw IllegalArgumentException("Test DTO not recognised")
                }
            }
        val mapper = ColumnUiNodeMapper(compositeUiNodeMapper)

        and("a ColumnUiNodeDto with children") {
            val dto =
                ColumnUiNodeDto(
                    id = "column-id",
                    children = listOf(OtherUiNodeDto),
                )

            When("the dto is mapped") {
                val uiNode = mapper.map(dto)

                Then("it should return a ColumnUiNode") {
                    uiNode.shouldBeInstanceOf<ColumnUiNode>()
                }

                Then("it should have the correct id") {
                    uiNode.id shouldBe "column-id"
                }

                Then("it should have correctly mapped children") {
                    uiNode.children shouldHaveSize 1
                    uiNode.children[0] shouldBe OtherUiNode
                }
            }
        }

        and("a ColumnUiNodeDto with no children") {
            val dto = ColumnUiNodeDto(id = "empty-column", children = emptyList())

            When("the dto is mapped") {
                val uiNode = mapper.map(dto)

                Then("it should have no children") {
                    uiNode.children.shouldBeEmpty()
                }
            }
        }

        and("a non-ColumnUiNodeDto") {
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
