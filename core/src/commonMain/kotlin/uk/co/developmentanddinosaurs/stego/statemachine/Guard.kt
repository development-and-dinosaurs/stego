package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Represents a condition that must be met for a [Transition] to be taken.
 *
 * This is a regular interface to allow library consumers to provide their own custom guard implementations.
 * Each guard is responsible for its own evaluation logic.
 */
interface Guard {
    /**
     * Evaluates the guard condition against the current state machine context and the triggering event.
     *
     * @param context The current, immutable context of the state machine.
     * @param event The event that triggered the potential transition.
     * @return `true` if the condition is met and the transition should be allowed, `false` otherwise.
     */
    fun evaluate(context: Context, event: Event): Boolean
}

/**
 * A guard that checks if two values are equal.
 */
data class EqualsGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return left.resolve(context, event) == right.resolve(context, event)
    }
}

/**
 * A guard that checks if two values are not equal.
 */
data class NotEqualsGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return left.resolve(context, event) != right.resolve(context, event)
    }
}

/**
 * A guard that checks if the left value is greater than the right value.
 */
data class GreaterThanGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    @Suppress("UNCHECKED_CAST")
    override fun evaluate(context: Context, event: Event): Boolean {
        val leftValue = left.resolve(context, event)
        val rightValue = right.resolve(context, event)
        if (leftValue == null || rightValue == null) {
            return false
        }
        val result = try {
            (leftValue as? Comparable<Any>)?.compareTo(rightValue)
        } catch (_: ClassCastException) {
            null
        }
        return result != null && result > 0
    }
}

/**
 * A guard that checks if the left value is less than the right value.
 */
data class LessThanGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    @Suppress("UNCHECKED_CAST")
    override fun evaluate(context: Context, event: Event): Boolean {
        val leftValue = left.resolve(context, event)
        val rightValue = right.resolve(context, event)
        if (leftValue == null || rightValue == null) {
            return false
        }
        val result = try {
            (leftValue as? Comparable<Any>)?.compareTo(rightValue)
        } catch (_: ClassCastException) {
            null
        }
        return result != null && result < 0
    }
}

/**
 * A guard that checks if the left value is greater than or equal to the right value.
 */
data class GreaterThanOrEqualsGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    @Suppress("UNCHECKED_CAST")
    override fun evaluate(context: Context, event: Event): Boolean {
        val leftValue = left.resolve(context, event)
        val rightValue = right.resolve(context, event)
        if (leftValue == null || rightValue == null) {
            return false
        }
        val result = try {
            (leftValue as? Comparable<Any>)?.compareTo(rightValue)
        } catch (_: ClassCastException) {
            null
        }
        return result != null && result >= 0
    }
}

/**
 * A guard that checks if the left value is less than or equal to the right value.
 */
data class LessThanOrEqualsGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    @Suppress("UNCHECKED_CAST")
    override fun evaluate(context: Context, event: Event): Boolean {
        val leftValue = left.resolve(context, event)
        val rightValue = right.resolve(context, event)
        if (leftValue == null || rightValue == null) {
            return false
        }
        val result = try {
            (leftValue as? Comparable<Any>)?.compareTo(rightValue)
        } catch (_: ClassCastException) {
            null
        }
        return result != null && result <= 0
    }
}

/**
 * A composite guard that evaluates to true only if all of its contained [guards] evaluate to true.
 */
data class AndGuard(val guards: List<Guard>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return guards.all { it.evaluate(context, event) }
    }
}

/**
 * A composite guard that evaluates to true if at least one of its contained [guards] evaluates to true.
 */
data class OrGuard(val guards: List<Guard>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return guards.any { it.evaluate(context, event) }
    }
}

/**
 * A guard that inverts the result of a single contained [guard].
 */
data class NotGuard(val guard: Guard) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return !guard.evaluate(context, event)
    }
}
