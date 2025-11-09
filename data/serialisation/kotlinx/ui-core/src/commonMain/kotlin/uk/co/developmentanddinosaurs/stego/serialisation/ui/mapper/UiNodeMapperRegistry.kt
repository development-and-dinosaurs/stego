package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode

class UiNodeMapperRegistry(uiNodeMappers: Set<UiNodeMapper>) {
  private val mappers = uiNodeMappers.associateBy { it.supportedType() }

  fun map(dto: UiNodeDto): UiNode {
    require(mappers.containsKey(dto.type))
    return mappers[dto.type]!!.map(dto, this)
  }
}
