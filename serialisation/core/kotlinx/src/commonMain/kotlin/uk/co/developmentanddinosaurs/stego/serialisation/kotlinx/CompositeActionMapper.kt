package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineException
import kotlin.reflect.KClass

/**
 * A composite [ActionDtoMapper] that holds a map of other mappers.
 * It looks up the correct mapper based on the DTO's class and delegates the mapping task.
 */
class CompositeActionMapper(
    private val mapperMap: Map<KClass<out ActionDto>, ActionDtoMapper>
) : ActionDtoMapper {
    override fun map(dto: ActionDto): Action {
        println("Mapping $dto")
        val mapper = mapperMap[dto::class]
            ?: throw StateMachineException("Unsupported ActionDto type: ${dto::class.simpleName}")
        return mapper.map(dto)
    }
}
