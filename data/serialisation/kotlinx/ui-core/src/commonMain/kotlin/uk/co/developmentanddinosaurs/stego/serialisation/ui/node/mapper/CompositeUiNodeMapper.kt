package uk.co.developmentanddinosaurs.stego.serialisation.ui.node.mapper

import kotlin.reflect.KClass
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode

/**
 * A [UiNodeMapper] that holds a map of other mappers. It looks up the correct mapper based on the
 * DTO's class and delegates the mapping task to it.
 */
class CompositeUiNodeMapper(
    simpleMappers: Map<KClass<out UiNodeDto>, UiNodeMapper>,
    compositeAwareFactories: Map<KClass<out UiNodeDto>, (UiNodeMapper) -> UiNodeMapper>,
) : UiNodeMapper {
  private val mapperMap: Map<KClass<out UiNodeDto>, UiNodeMapper> =
      simpleMappers + compositeAwareFactories.mapValues { it.value(this) }

  override fun map(dto: UiNodeDto): UiNode {
    val mapper =
        mapperMap[dto::class]
            ?: throw IllegalArgumentException(
                "Unsupported UiNodeDto type: ${dto::class.simpleName}"
            )
    return mapper.map(dto)
  }
}
