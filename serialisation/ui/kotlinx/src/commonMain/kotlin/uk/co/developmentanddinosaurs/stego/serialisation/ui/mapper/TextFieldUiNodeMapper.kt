package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.TextFieldUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.TextFieldUiNode
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode


class TextFieldUiNodeMapper : UiNodeMapper {
    override fun map(dto: UiNodeDto): UiNode {
        require(dto is TextFieldUiNodeDto)
        return TextFieldUiNode(
            text = dto.text,
            label = dto.label,
            onTextChanged = dto.onTextChanged.toDomain()
        )
    }
}
