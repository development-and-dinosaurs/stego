package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.serialisation.ui.OtherUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ImageUiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.ImageUiNode

class ImageUiNodeDtoMapperTest : BehaviorSpec({
    Given("an ImageUiNodeDtoMapper") {
        val mapper = ImageUiNodeDtoMapper()

        and("an ImageUiNodeDto") {
            val dto =
                ImageUiNodeDto(
                    id = "image-id",
                    url = "https://dinos.co.uk/stego.png",
                    contentDescription = "A friendly stegosaurus",
                )

            When("the dto is mapped") {
                val uiNode = mapper.map(dto)

                Then("it should map all properties correctly") {
                    uiNode.shouldBeInstanceOf<ImageUiNode>()
                    uiNode.id shouldBe "image-id"
                    uiNode.url shouldBe "https://dinos.co.uk/stego.png"
                    uiNode.contentDescription shouldBe "A friendly stegosaurus"
                }
            }
        }

        and("a non-ImageUiNodeDto") {
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
