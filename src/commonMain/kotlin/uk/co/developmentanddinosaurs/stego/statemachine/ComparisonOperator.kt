package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * Defines the set of operators that can be used within a [ComparisonGuard]
 * to compare two values.
 */
enum class ComparisonOperator {
    /** Checks for equality (e.g., a == b). */
    EQUALS,

    /** Checks for inequality (e.g., a != b). */
    NOT_EQUALS,

    /** Checks if the left value is greater than the right (e.g., a > b). */
    GREATER_THAN,

    /** Checks if the left value is less than the right (e.g., a < b). */
    LESS_THAN,

    /** Checks if the left value is greater than or equal to the right (e.g., a >= b). */
    GREATER_THAN_OR_EQUALS,

    /** Checks if the left value is less than or equal to the right (e.g., a <= b). */
    LESS_THAN_OR_EQUALS
}
