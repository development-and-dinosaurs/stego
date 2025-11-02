package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UserInteractionDto
import uk.co.developmentanddinosaurs.stego.ui.node.UserInteraction

/**
 * Maps a [UserInteractionDto] to a [UserInteraction] domain model.
 */
class UserInteractionMapper {
    fun map(dto: UserInteractionDto): UserInteraction = UserInteraction(trigger = dto.trigger)
}
