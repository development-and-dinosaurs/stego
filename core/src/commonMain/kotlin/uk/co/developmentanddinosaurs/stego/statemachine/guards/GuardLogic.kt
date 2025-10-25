package uk.co.developmentanddinosaurs.stego.statemachine.guards

import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.valueresolution.ValueProvider

/**
 * Performs a comparison by fetching values from providers and executing a given comparison operation.
 * This function encapsulates the common logic for all comparison-based guards.
 *
 * @param left The raw left-hand value (either a literal or a string like `{context.key}`).
 * @param right The raw right-hand value.
 * @param context The state machine context.
 * @param event The triggering event.
 * @param operation A lambda that defines the comparison logic (e.g., less than, greater than).
 * @return The boolean result of the comparison.
 */
internal fun performComparison(
    left: ValueProvider,
    right: ValueProvider,
    context: Context,
    event: Event,
    operation: (left: Comparable<Any>, right: Any) -> Boolean,
): Boolean {
    val leftValue = left.get(context, event)
    val rightValue = right.get(context, event)

    require(leftValue != null) { "Left value cannot be null." }
    require(rightValue != null) { "Right value cannot be null." }

    require(leftValue is Comparable<*>) { "Left value of type '${leftValue::class.simpleName}' is not Comparable." }

    return try {
        @Suppress("UNCHECKED_CAST")
        operation(leftValue as Comparable<Any>, rightValue)
    } catch (e: ClassCastException) {
        throw IllegalStateException(
            "Left type '${leftValue::class.simpleName}' and right type '${rightValue::class.simpleName}' are not comparable.",
            e,
        )
    }
}

/**
 * Performs an equality check by fetching values from providers.
 * This function encapsulates the common logic for all equality-based guards.
 *
 * @param left The raw left-hand value.
 * @param right The raw right-hand value.
 * @param context The state machine context.
 * @param event The triggering event.
 * @return The boolean result of the equality check.
 */
internal fun performEqualityCheck(
    left: ValueProvider,
    right: ValueProvider,
    context: Context,
    event: Event,
): Boolean {
    val leftValue = left.get(context, event)
    val rightValue = right.get(context, event)

    return leftValue == rightValue
}
