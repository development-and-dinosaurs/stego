package uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper

import kotlin.reflect.KClass
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonActionDto
import uk.co.developmentanddinosaurs.stego.ui.node.ButtonAction

class CompositeButtonActionMapper(
    private val mappers: Map<KClass<out ButtonActionDto>, ButtonActionMapper>,
) : ButtonActionMapper {
  override fun map(dto: ButtonActionDto): ButtonAction {
    val mapper =
        mappers[dto::class]
            ?: throw IllegalArgumentException(
                "Unsupported ButtonActionDto type: ${dto::class.simpleName}"
            )
    return mapper.map(dto)
  }
}
