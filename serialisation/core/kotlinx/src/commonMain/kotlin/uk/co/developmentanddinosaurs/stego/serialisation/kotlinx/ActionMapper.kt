package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action

interface ActionMapper {
    fun canMap(actionDto: ActionDto): Boolean
    fun map(actionDto: ActionDto): Action
}
