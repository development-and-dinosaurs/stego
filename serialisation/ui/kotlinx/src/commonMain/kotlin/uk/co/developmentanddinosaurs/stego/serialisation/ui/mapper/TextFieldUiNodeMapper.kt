package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.TextFieldUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.TextFieldUiNode


class TextFieldUiNodeMapper(
    private val interactionMapper: InteractionMapper
) : UiNodeMapper {
    override fun map(dto: UiNodeDto): TextFieldUiNode {
        require(dto is TextFieldUiNodeDto)
        return TextFieldUiNode(
            id = dto.id,
            text = dto.text,
            label = dto.label,
            onTextChanged = interactionMapper.map(dto.onTextChanged)
        )
    }
}
