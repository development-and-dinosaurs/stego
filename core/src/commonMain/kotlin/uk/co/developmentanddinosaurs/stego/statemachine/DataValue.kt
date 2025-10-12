package uk.co.developmentanddinosaurs.stego.statemachine

/**
 * A sealed interface representing any valid data value within the state machine.
 * This can be a primitive, an object, or a list.
 */
sealed interface DataValue

/**
 * A sealed sub-interface for primitive data values that can be compared.
 */
sealed interface Primitive : DataValue, Comparable<Primitive> {
    override fun compareTo(other: Primitive): Int
}

/**
 * A sub-interface for primitive types that can be treated as numbers.
 */
sealed interface NumericPrimitive : Primitive {
    fun toDouble(): Double
    override fun compareTo(other: Primitive): Int {
        if (other is NumericPrimitive) {
            return this.toDouble().compareTo(other.toDouble())
        } else {
            throw IllegalArgumentException("Cannot compare a NumericPrimitive with a ${other::class.simpleName}")
        }
    }
}

data class StringPrimitive(val value: String) : Primitive {
    override fun compareTo(other: Primitive): Int {
        if (other is StringPrimitive) {
            return this.value.compareTo(other.value)
        } else {
            throw IllegalArgumentException("Cannot compare a StringPrimitive with a ${other::class.simpleName}")
        }
    }
}

data class IntPrimitive(val value: Int) : NumericPrimitive {
    override fun toDouble(): Double = value.toDouble()
}

data class LongPrimitive(val value: Long) : NumericPrimitive {
    override fun toDouble(): Double = value.toDouble()
}

data class FloatPrimitive(val value: Float) : NumericPrimitive {
    override fun toDouble(): Double = value.toDouble()
}

data class DoublePrimitive(val value: Double) : NumericPrimitive {
    override fun toDouble(): Double = value
}

data class BooleanPrimitive(val value: Boolean) : Primitive {
    override fun compareTo(other: Primitive): Int {
        if (other is BooleanPrimitive) {
            return this.value.compareTo(other.value)
        } else {
            throw IllegalArgumentException("Cannot compare a BooleanPrimitive with a ${other::class.simpleName}")
        }
    }
}

/**
 * Represents a JSON-style object: a map of string keys to other [DataValue] instances.
 */
data class ObjectValue(val value: Map<String, DataValue>) : DataValue

/**
 * Represents a JSON-style array: a list of other [DataValue] instances.
 */
data class ListValue(val value: List<DataValue>) : DataValue
