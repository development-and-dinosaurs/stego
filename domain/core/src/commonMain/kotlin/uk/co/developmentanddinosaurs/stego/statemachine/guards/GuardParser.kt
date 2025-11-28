package uk.co.developmentanddinosaurs.stego.statemachine.guards

import uk.co.developmentanddinosaurs.stego.statemachine.valueresolution.ValueProvider

/**
 * A recursive descent parser for creating composite guards from a string expression.
 *
 * This parser implements a specific grammar with the following rules:
 *
 * 1.  **Strict Parenthesizing**: All expressions, simple or composite, must be enclosed in parentheses,
 *     with the only exception being the unary `NOT` (`!`) operator at the start of an expression.
 *     - Valid: `(a < b)`, `((a < b) && (c > d))`, `!(a < b)`
 *     - Invalid: `a < b`, `(a < b) && (c > d)`
 *
 * 2.  **Operator Precedence** (from highest to lowest):
 *     - `()`: Grouping
 *     - `!`: Logical NOT
 *     - `&&`: Logical AND
 *     - `||`: Logical OR
 *     - `==, !=, <, <=, >, >=`: Comparison operators
 *
 * 3.  **Operands**: Can be literals (e.g., `5`, `"hello"`, `true`) or dynamic values resolved
 *     from context (`{context.key}`) or events (`{event.key}`).
 */
internal object GuardParser {
    /** Operator map for simple comparisons. The order is critical to ensure longest-match-first (e.g., '>=' before '>'). */
    private val COMPARISON_OPERATOR_MAP =
        linkedMapOf(
            "==" to ::EqualsGuard,
            "!=" to ::NotEqualsGuard,
            "<=" to ::LessThanOrEqualToGuard,
            ">=" to ::GreaterThanOrEqualToGuard,
            "<" to ::LessThanGuard,
            ">" to ::GreaterThanGuard,
        )

    /**
     * Parses a string expression into a [Guard] object.
     *
     * This is the main entry point for the parser. It recursively breaks down the expression
     * according to the defined grammar rules (parenthesizing, operator precedence).
     *
     * @param expression The string expression to parse.
     * @return A [Guard] instance representing the expression logic.
     * @throws IllegalArgumentException if the expression has invalid syntax or unbalanced parentheses.
     */
    fun parse(expression: String): Guard {
        val trimmed = expression.trim()
        require(!(trimmed.isBlank())) { "Expression cannot be empty." }
        validateParentheses(trimmed)

        // A valid expression must either be a NOT expression or be fully enclosed in parentheses.
        if (trimmed.startsWith("!")) {
            return NotGuard(parse(trimmed.substring(1)))
        }

        require(isEnclosedByParentheses(trimmed)) {
            "All expressions must be enclosed in parentheses or be a NOT expression: '$expression'"
        }

        // The content inside the parentheses is a new sub-expression to be parsed.
        val innerExpression = trimmed.substring(1, trimmed.length - 1).trim()

        // Check for composite operators within the sub-expression, from lowest to highest precedence.
        findSplitPoint(innerExpression, "||")?.let {
            return OrGuard(parse(innerExpression.substring(0, it)), parse(innerExpression.substring(it + 2).trim()))
        }
        findSplitPoint(innerExpression, "&&")?.let {
            return AndGuard(parse(innerExpression.substring(0, it)), parse(innerExpression.substring(it + 2).trim()))
        }

        // If no composite operators are found, it must be a simple comparison.
        COMPARISON_OPERATOR_MAP.forEach { (op, constructor) ->
            findSplitPoint(innerExpression, op)?.let {
                val left = ValueProvider.resolve(innerExpression.substring(0, it).trim())
                val right = ValueProvider.resolve(innerExpression.substring(it + op.length).trim())
                return constructor(left, right)
            }
        }
        // If the content inside the parentheses is not a valid composite or simple expression, recurse.
        // This handles multiple layers of parentheses like `((a < b))`.
        return parse(innerExpression)
    }

    /**
     * Finds the index of a binary operator (like '&&' or '||') at the top level of an expression,
     * ignoring operators inside parentheses. It searches from right to left.
     */
    private fun findSplitPoint(
        expression: String,
        operator: String,
    ): Int? {
        var parenDepth = 0
        for (i in expression.indices.reversed()) {
            if (parenDepth == 0 && expression.substring(i).startsWith(operator)) {
                return i
            }

            when (expression[i]) {
                ')' -> parenDepth++
                '(' -> parenDepth--
            }
        }
        return null
    }

    /**
     * Checks if an expression is fully and exclusively enclosed by a single pair of matching parentheses.
     * For example, `(a && b)` is true, but `(a) && (b)` is false.
     */
    private fun isEnclosedByParentheses(expression: String): Boolean {
        var parenDepth = 0
        for (i in 0 until expression.length - 1) {
            when (expression[i]) {
                '(' -> parenDepth++
                ')' -> parenDepth--
            }
            if (parenDepth == 0) return false
        }
        return true
    }

    /**
     * Validates that the parentheses in the expression are balanced.
     *
     * @throws IllegalArgumentException if an unexpected `)` is found or if a `(` is not closed.
     */
    private fun validateParentheses(expression: String) {
        var parenDepth = 0
        for (char in expression) {
            when (char) {
                '(' -> parenDepth++
                ')' -> parenDepth--
            }
            require(parenDepth >= 0) { "Mismatched parentheses: Unexpected ')' in '$expression'" }
        }
        require(parenDepth == 0) { "Mismatched parentheses: Missing ')' in '$expression'" }
    }
}
