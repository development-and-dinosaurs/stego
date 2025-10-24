package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.InteractionDto
import uk.co.developmentanddinosaurs.stego.ui.node.UserInteraction

/**
 * Maps an [InteractionDto] to a [UserInteraction] domain model.
 */
class InteractionMapper {
    fun map(dto: InteractionDto): UserInteraction = UserInteraction(trigger = dto.trigger)
}