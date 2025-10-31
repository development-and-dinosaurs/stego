package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.LabelUiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.LabelUiNode

class LabelUiNodeMapperTest : BehaviorSpec({
    Given("a LabelUiNodeMapper") {
        val mapper = LabelUiNodeMapper()

        and("a LabelUiNodeDto") {
            val dto = LabelUiNodeDto(
                id = "label-id",
                text = "Hello, Stego!"
            )

            When("the dto is mapped") {
                val uiNode = mapper.map(dto)

                Then("it should map all properties correctly") {
                    uiNode.shouldBeInstanceOf<LabelUiNode>()
                    uiNode.id shouldBe "label-id"
                    uiNode.text shouldBe "Hello, Stego!"
                }
            }
        }

        and("a non-LabelUiNodeDto") {
            val dto = OtherUiNodeDto

            When("the dto is mapped") {
                Then("it should throw an IllegalArgumentException") {
                    val exception = shouldThrow<IllegalArgumentException> {
                        mapper.map(dto)
                    }
                    exception.message shouldBe "Failed requirement."
                }
            }
        }
    }
})
