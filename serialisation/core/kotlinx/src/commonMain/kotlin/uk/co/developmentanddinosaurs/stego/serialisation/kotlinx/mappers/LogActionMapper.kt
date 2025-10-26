package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.ActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LogActionDto
import uk.co.developmentanddinosaurs.stego.statemachine.Action
import uk.co.developmentanddinosaurs.stego.statemachine.LogAction

/**
 * A specific mapper responsible for converting a [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LogActionDto] into a [LogAction].
 *
 * This mapper is designed for a single purpose and will fail if provided with any DTO
 * other than [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LogActionDto]. It is typically used as part of a larger mapping strategy,
 * for example, within the [ActionMapper] registry.
 *
 * @param logger A lambda that defines the logging implementation (e.g., `println`, `Log.d`, etc.).
 * Defaults to `println`.
 */
class LogActionMapper(
    private val logger: (String) -> Unit = ::println,
) : ActionDtoMapper {
    /**
     * Maps a [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LogActionDto] to its domain [LogAction] counterpart.
     *
     * @param dto The data transfer object to map. Must be an instance of [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LogActionDto].
     * @return The corresponding domain [LogAction] object.
     * @throws IllegalArgumentException if the provided `dto` is not a [uk.co.developmentanddinosaurs.stego.serialisation.kotlinx.LogActionDto].
     */
    override fun map(dto: ActionDto): Action {
        require(dto is LogActionDto) { "LogActionMapper can only map LogActionDto" }
        return LogAction(dto.message, logger)
    }
}
