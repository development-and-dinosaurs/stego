package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode

/** Defines a contract for mapping a [UiNodeDto] to a domain [UiNode]. */
interface UiNodeMapper {

  fun supportedType(): String

  /** Maps a [UiNodeDto] to a [UiNode]. */
  fun map(dto: UiNodeDto, registry: UiNodeMapperRegistry): UiNode
}
