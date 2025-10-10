package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a condition that must be met for a [Transition] to be taken.
 *
 * This is a regular interface to allow library consumers to provide their own custom guard implementations.
 * The library provides a set of standard, serializable guard types for common logical operations.
 */
interface Guard

/**
 * A composite guard that evaluates to true only if all of its contained [guards] evaluate to true.
 * This represents a logical AND operation.
 *
 * @property guards The list of guards to be evaluated.
 */
data class AndGuard(
    val guards: List<Guard>
) : Guard

/**
 * A composite guard that evaluates to true if at least one of its contained [guards] evaluates to true.
 * This represents a logical OR operation.
 *
 * @property guards The list of guards to be evaluated.
 */
data class OrGuard(
    val guards: List<Guard>
) : Guard

/**
 * A guard that inverts the result of a single contained [guard].
 * This represents a logical NOT operation.
 *
 * @property guard The guard whose result will be negated.
 */
data class NotGuard(
    val guard: Guard
) : Guard

/**
 * A guard that performs a comparison between two values.
 *
 * The values for the comparison can be sourced from a literal, the context, or the triggering event,
 * as defined by the [Value] interface.
 *
 * @property left The left-hand side [Value] for the comparison.
 * @property operator The [ComparisonOperator] used to compare the left and right values.
 * @property right The right-hand side [Value] for the comparison.
 */
data class ComparisonGuard(
    val left: Value<Any>,
    val operator: ComparisonOperator,
    val right: Value<Any>
) : Guard
