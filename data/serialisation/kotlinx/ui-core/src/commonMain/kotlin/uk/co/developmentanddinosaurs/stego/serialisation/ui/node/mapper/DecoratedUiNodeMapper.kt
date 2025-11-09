package uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.DecoratedUiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.DecoratedUiNode

interface DecoratedUiNodeMapper {
  fun map(dto: DecoratedUiNodeDto): DecoratedUiNode
}
