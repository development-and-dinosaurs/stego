package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action

class CompositeActionMapper(private val mappers: List<ActionMapper>) : ActionMapper {

    override fun canMap(actionDto: ActionDto): Boolean {
        return mappers.any { it.canMap(actionDto) }
    }

    override fun map(actionDto: ActionDto): Action {
        for (mapper in mappers) {
            if (mapper.canMap(actionDto)) {
                return mapper.map(actionDto)
            }
        }
        throw IllegalArgumentException("No ActionMapper found for ActionDto: ${actionDto::class.simpleName}")
    }
}
