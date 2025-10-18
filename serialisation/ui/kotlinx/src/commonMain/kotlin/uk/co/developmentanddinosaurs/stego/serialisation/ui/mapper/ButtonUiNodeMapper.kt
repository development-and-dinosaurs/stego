package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode


class ButtonUiNodeMapper : UiNodeMapper {
    override fun map(dto: UiNodeDto): UiNode {
        require(dto is ButtonUiNodeDto)
        return ButtonUiNode(
            text = dto.text,
            onClick = dto.onClick.toDomain()
        )
    }
}
