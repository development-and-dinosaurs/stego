package uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode

/**
 * Defines a contract for mapping a
 * [uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto] to a domain
 * [uk.co.developmentanddinosaurs.stego.ui.node.UiNode].
 */
fun interface UiNodeMapper {
  /**
   * Maps a [uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto] to a
   * [uk.co.developmentanddinosaurs.stego.ui.node.UiNode].
   */
  fun map(dto: UiNodeDto): UiNode
}
