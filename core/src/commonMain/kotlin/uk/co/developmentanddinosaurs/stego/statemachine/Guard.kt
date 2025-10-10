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
 * Resolves a [Value] instance to its concrete value from the given [context] or [event].
 */
private fun resolveValue(value: Value<*>, context: Context, event: Event): Any {
    return when (value) {
        is ContextValue -> context.get(value.path)
        is EventValue<*> -> event.data[value.path]!!
        is LiteralValue<*> -> value.value!!
    }
}

/**
 * Compares two values using a provided comparison function.
 *
 * This helper function resolves the concrete values of [left] and [right], then attempts to cast them to [Comparable].
 * If the cast is successful, it invokes the [comparison] lambda with the result of the comparison.
 * The cast is suppressed because the check is handled gracefully, returning `null` on [ClassCastException].
 *
 * @return `true` if the comparison is successful and the lambda returns `true`, `false` otherwise.
 */
@Suppress("UNCHECKED_CAST")
private fun compareValues(
    left: Value<Any>,
    right: Value<Any>,
    context: Context,
    event: Event,
    comparison: (Int) -> Boolean
): Boolean {
    val leftValue = resolveValue(left, context, event)
    val rightValue = resolveValue(right, context, event)
    val result = try {
        (leftValue as? Comparable<Any>)?.compareTo(rightValue)
    } catch (_: ClassCastException) {
        null
    }
    return result != null && comparison(result)
}


// region Value Guards

/**
 * A guard that checks if two values are equal.
 */
data class EqualsGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return resolveValue(left, context, event) == resolveValue(right, context, event)
    }
}

/**
 * A guard that checks if two values are not equal.
 */
@Suppress("unused")
data class NotEqualsGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return resolveValue(left, context, event) != resolveValue(right, context, event)
    }
}

/**
 * A guard that checks if the left value is greater than the right value.
 * Evaluates to `false` if the values are not comparable.
 */
data class GreaterThanGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return compareValues(left, right, context, event) { it > 0 }
    }
}

/**
 * A guard that checks if the left value is less than the right value.
 * Evaluates to `false` if the values are not comparable.
 */
@Suppress("unused")
data class LessThanGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return compareValues(left, right, context, event) { it < 0 }
    }
}

/**
 * A guard that checks if the left value is greater than or equal to the right value.
 * Evaluates to `false` if the values are not comparable.
 */
@Suppress("unused")
data class GreaterThanOrEqualsGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return compareValues(left, right, context, event) { it >= 0 }
    }
}

/**
 * A guard that checks if the left value is less than or equal to the right value.
 * Evaluates to `false` if the values are not comparable.
 */
@Suppress("unused")
data class LessThanOrEqualsGuard(val left: Value<Any>, val right: Value<Any>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return compareValues(left, right, context, event) { it <= 0 }
    }
}

// endregion

// region Logical Guards

/**
 * A composite guard that evaluates to `true` only if all of its contained [guards] evaluate to `true`.
 * This guard short-circuits, meaning it stops evaluating as soon as one of the sub-guards returns `false`.
 */
data class AndGuard(val guards: List<Guard>) : Guard {
    override fun evaluate(context: Context, event: Event): Boolean {
        return guards.all { it.evaluate(context, event) }
    }
}

/**
 * A composite guard that evaluates to `true` if at least one of its contained [guards] evaluates to `true`.
 * This guard short-circuits, meaning it stops evaluating as soon as one of the sub-guards returns `true`.
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

// endregion