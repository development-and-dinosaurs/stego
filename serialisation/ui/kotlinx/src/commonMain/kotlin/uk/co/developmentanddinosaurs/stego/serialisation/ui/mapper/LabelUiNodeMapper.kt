package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.LabelUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.LabelUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode


class LabelUiNodeMapper : UiNodeMapper {
    override fun map(dto: UiNodeDto): UiNode {
        require(dto is LabelUiNodeDto)
        return LabelUiNode(text = dto.text)
    }
}
