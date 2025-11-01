package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ProgressIndicatorUiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.ProgressIndicatorUiNode

class ProgressIndicatorUiNodeMapperTest : BehaviorSpec({
    Given("a ProgressIndicatorUiNodeMapper") {
        val mapper = ProgressIndicatorUiNodeMapper()

        and("a ProgressIndicatorUiNodeDto") {
            val dto = ProgressIndicatorUiNodeDto(id = "progress-id")

            When("the dto is mapped") {
                val uiNode = mapper.map(dto)

                Then("it should map all properties correctly") {
                    uiNode.shouldBeInstanceOf<ProgressIndicatorUiNode>()
                    uiNode.id shouldBe "progress-id"
                }
            }
        }

        and("a non-ProgressIndicatorUiNodeDto") {
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
