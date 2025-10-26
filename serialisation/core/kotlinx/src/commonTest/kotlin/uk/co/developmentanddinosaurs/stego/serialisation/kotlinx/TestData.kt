package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

/**
 * A test-specific DTO for verifying action mapping.
 */
internal data class TestActionDto(val data: String) : ActionDto

/**
 * A dummy DTO used for failure case testing in mappers.
 */
internal data class OtherActionDto(val data: String) : ActionDto

/**
 * A test-specific Action for verifying action mapping.
 */
internal data class TestAction(val data: String) : Action {
    override fun execute(
        context: Context,
        event: Event,
    ): Context = context
}