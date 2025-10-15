package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a condition that must be met for a [Transition] to be taken.
 */
interface Guard {
    fun evaluate(
        context: Context,
        event: Event,
    ): Boolean
}

/**
 * A guard that checks if two values are equal.
 */
data class EqualsGuard(
    val left: ValueReference,
    val right: ValueReference,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean = left.resolve(context, event) == right.resolve(context, event)
}

/**
 * A guard that checks if two values are not equal.
 */
data class NotEqualsGuard(
    val left: ValueReference,
    val right: ValueReference,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean = left.resolve(context, event) != right.resolve(context, event)
}

/**
 * A guard that checks if the left value is greater than the right value.
 */
data class GreaterThanGuard(
    val left: ValueReference,
    val right: ValueReference,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean {
        val leftValue = left.resolve(context, event)
        val rightValue = right.resolve(context, event)
        if (leftValue !is Primitive || rightValue !is Primitive) return false
        return leftValue > rightValue
    }
}

/**
 * A guard that checks if the left value is less than the right value.
 */
data class LessThanGuard(
    val left: ValueReference,
    val right: ValueReference,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean {
        val leftValue = left.resolve(context, event)
        val rightValue = right.resolve(context, event)
        if (leftValue !is Primitive || rightValue !is Primitive) return false
        return leftValue < rightValue
    }
}

/**
 * A guard that checks if the left value is greater than or equal to the right value.
 */
data class GreaterThanOrEqualsGuard(
    val left: ValueReference,
    val right: ValueReference,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean {
        val leftValue = left.resolve(context, event)
        val rightValue = right.resolve(context, event)
        if (leftValue == rightValue) return true
        if (leftValue !is Primitive || rightValue !is Primitive) return false
        return leftValue >= rightValue
    }
}

/**
 * A guard that checks if the left value is less than or equal to the right value.
 */
data class LessThanOrEqualsGuard(
    val left: ValueReference,
    val right: ValueReference,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean {
        val leftValue = left.resolve(context, event)
        val rightValue = right.resolve(context, event)
        if (leftValue == rightValue) return true
        if (leftValue !is Primitive || rightValue !is Primitive) return false
        return leftValue <= rightValue
    }
}

/**
 * A composite guard that evaluates to true only if all of its contained [guards] evaluate to true.
 */
data class AndGuard(
    val guards: List<Guard>,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean = guards.all { it.evaluate(context, event) }
}

/**
 * A composite guard that evaluates to true if at least one of its contained [guards] evaluates to true.
 */
data class OrGuard(
    val guards: List<Guard>,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean = guards.any { it.evaluate(context, event) }
}

/**
 * A guard that inverts the result of a single contained [guard].
 */
data class NotGuard(
    val guard: Guard,
) : Guard {
    override fun evaluate(
        context: Context,
        event: Event,
    ): Boolean = !guard.evaluate(context, event)
}
