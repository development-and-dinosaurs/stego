package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ImageUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.ImageUiNode


class ImageUiNodeDtoMapper() : UiNodeMapper {
    override fun map(dto: UiNodeDto): ImageUiNode {
        require(dto is ImageUiNodeDto)
        return ImageUiNode(
            dto.id,
            dto.url,
            dto.contentDescription
        )
    }
}
