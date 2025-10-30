package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ColumnUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.ColumnUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode


class ColumnUiNodeMapper(private val compositeUiNodeMapper: UiNodeMapper) : UiNodeMapper {
    override fun map(dto: UiNodeDto): UiNode {
        require(dto is ColumnUiNodeDto)
        return ColumnUiNode(
            id = dto.id,
            children = dto.children.map { compositeUiNodeMapper.map(it) },
        )
    }
}
