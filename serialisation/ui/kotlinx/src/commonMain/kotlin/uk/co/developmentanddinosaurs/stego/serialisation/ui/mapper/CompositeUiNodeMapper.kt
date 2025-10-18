package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.ui.node.UiNode
import kotlin.reflect.KClass

/**
 * A [UiNodeMapper] that holds a map of other mappers.
 * It looks up the correct mapper based on the DTO's class and delegates the mapping task to it.
 */
class CompositeUiNodeMapper(
    simpleMappers: Map<KClass<out UiNodeDto>, UiNodeMapper>,
    compositeAwareFactories: Map<KClass<out UiNodeDto>, (UiNodeMapper) -> UiNodeMapper>
) : UiNodeMapper {
    private val mapperMap: Map<KClass<out UiNodeDto>, UiNodeMapper>

    init {
        val compositeAwareMappers = compositeAwareFactories.mapValues { it.value(this) }
        this.mapperMap = simpleMappers + compositeAwareMappers
    }

    override fun map(dto: UiNodeDto): UiNode {
        val mapper = mapperMap[dto::class]
            ?: throw IllegalArgumentException("Unsupported UiNodeDto type: ${dto::class.simpleName}")
        return mapper.map(dto)
    }
}
