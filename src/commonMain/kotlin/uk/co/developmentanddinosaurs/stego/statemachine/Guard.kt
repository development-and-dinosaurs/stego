package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a condition that must be met for a [Transition] to be taken.
 *
 * Guards are evaluated when an event is processed and before a transition is executed.
 * This is a sealed interface, allowing for a declarative, JSON-friendly definition of complex logical conditions.
 */
sealed interface Guard

/**
 * A composite guard that evaluates to true only if all of its contained [guards] evaluate to true.
 * This represents a logical AND operation.
 */
interface AndGuard : Guard {
    /** The list of guards to be evaluated. */
    val guards: List<Guard>
}

/**
 * A composite guard that evaluates to true if at least one of its contained [guards] evaluates to true.
 * This represents a logical OR operation.
 */
interface OrGuard : Guard {
    /** The list of guards to be evaluated. */
    val guards: List<Guard>
}

/**
 * A guard that inverts the result of a single contained [guard].
 * This represents a logical NOT operation.
 */
interface NotGuard : Guard {
    /** The guard whose result will be negated. */
    val guard: Guard
}

/**
 * A guard that performs a comparison between two values.
 *
 * The values for the comparison can be sourced from a literal, the [Context], or the triggering [Event],
 * as defined by the [Value] interface.
 */
interface ComparisonGuard : Guard {
    /** The left-hand side [Value] for the comparison. */
    val left: Value<Any>

    /** The [ComparisonOperator] used to compare the left and right values. */
    val operator: ComparisonOperator

    /** The right-hand side [Value] for the comparison. */
    val right: Value<Any>
}
