package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UserInteractionDto

class InteractionMapperTest :
    BehaviorSpec({
        Given("an InteractionMapper") {
            val mapper = UserInteractionMapper()

            and("a UserInteractionDto") {
                val dto = UserInteractionDto(trigger = "some-event")

                When("the dto is mapped") {
                    val interaction = mapper.map(dto)

                    Then("it should map the trigger correctly") {
                        interaction.trigger shouldBe "some-event"
                    }
                }
            }
        }
    })
