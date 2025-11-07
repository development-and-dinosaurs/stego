package uk.co.developmentanddinosaurs.stego.serialisation.mappers

import uk.co.developmentanddinosaurs.stego.serialisation.ActionDto
import uk.co.developmentanddinosaurs.stego.statemachine.Action

/**
 * A functional interface that defines a contract for mapping a specific [ActionDto] to its
 * corresponding domain [Action].
 *
 * This is the core component used in the [ActionMapper] registry to handle the conversion of
 * different action types.
 *
 * ### Implementation as a Class
 * For mappers with their own dependencies or complex logic, you can implement this interface as a
 * full class:
 * ```
 * class MyCustomActionMapper(private val dependency: MyService) : ActionDtoMapper {
 *     override fun map(dto: ActionDto): Action {
 *         require(dto is MyCustomActionDto)
 *         // ... logic using dependency ...
 *         return MyCustomAction(dto.data)
 *     }
 * }
 * ```
 *
 * ### Implementation as a Lambda
 * Because this is a `fun interface`, for simple, self-contained mappers, you can provide a concise
 * lambda directly in the [ActionMapper] registry:
 * ```
 * val mappers = mapOf(
 *     MyCustomActionDto::class to ActionDtoMapper { dto ->
 *         MyCustomAction((dto as MyCustomActionDto).data)
 *     }
 * )
 * ```
 *
 * @see ActionMapper
 */
fun interface ActionDtoMapper {
  /**
   * Maps the given [ActionDto] to an [Action].
   *
   * Implementations should typically validate that the `dto` is of the expected type.
   *
   * @param dto The data transfer object to map.
   * @return The resulting domain [Action].
   */
  fun map(dto: ActionDto): Action
}
