package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonUiNode

class ButtonUiNodeMapper(
    private val buttonActionMapper: ButtonActionMapper,
) : UiNodeMapper {
    override fun map(dto: UiNodeDto): ButtonUiNode {
        require(dto is ButtonUiNodeDto)
        return ButtonUiNode(
            id = dto.id,
            text = dto.text,
            onClick = buttonActionMapper.map(dto.onClick),
        )
    }
}
