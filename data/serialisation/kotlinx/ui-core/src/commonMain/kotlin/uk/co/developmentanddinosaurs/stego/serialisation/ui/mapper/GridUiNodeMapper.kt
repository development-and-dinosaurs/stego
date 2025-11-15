package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.GridUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.GridUiNode

class GridUiNodeMapper(
    private val compositeUiNodeMapper: UiNodeMapper,
) : UiNodeMapper {
    override fun map(dto: UiNodeDto): GridUiNode {
        require(dto is GridUiNodeDto)
        return GridUiNode(
            id = dto.id,
            columns = dto.columns,
            children = dto.children.map { compositeUiNodeMapper.map(it) },
        )
    }
}
