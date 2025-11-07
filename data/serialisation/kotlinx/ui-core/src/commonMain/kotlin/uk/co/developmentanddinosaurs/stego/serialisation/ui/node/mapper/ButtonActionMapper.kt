package uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonActionDto
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonAction

/**
 * Defines a contract for mapping a
 * [uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonActionDto] to a domain
 * [uk.co.developmentanddinosaurs.stego.ui.node.ButtonAction].
 */
fun interface ButtonActionMapper {
  fun map(dto: ButtonActionDto): ButtonAction
}
