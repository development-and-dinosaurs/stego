package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonActionDto
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonAction

/**
 * Defines a contract for mapping a [ButtonActionDto] to a domain [ButtonAction].
 */
fun interface ButtonActionMapper {
    fun map(dto: ButtonActionDto): ButtonAction
}
