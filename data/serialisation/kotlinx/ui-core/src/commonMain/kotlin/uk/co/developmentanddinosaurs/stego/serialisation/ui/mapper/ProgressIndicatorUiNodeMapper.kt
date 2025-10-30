package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ProgressIndicatorUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.ProgressIndicatorUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode


class ProgressIndicatorUiNodeMapper : UiNodeMapper {
    override fun map(dto: UiNodeDto): UiNode {
        require(dto is ProgressIndicatorUiNodeDto)
        return ProgressIndicatorUiNode(id = dto.id)
    }
}
