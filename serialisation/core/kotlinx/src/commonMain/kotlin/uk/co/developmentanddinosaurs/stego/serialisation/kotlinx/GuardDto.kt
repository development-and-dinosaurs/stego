package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.guards.*

@Serializable
sealed interface GuardDto {
    fun toDomain(): Guard
}

@Serializable
data class AndGuardDto(val guards: List<GuardDto>) : GuardDto {
    override fun toDomain(): Guard = AndGuard(*guards.map { it.toDomain() }.toTypedArray())
}

@Serializable
data class OrGuardDto(val guards: List<GuardDto>) : GuardDto {
    override fun toDomain(): Guard = OrGuard(*guards.map { it.toDomain() }.toTypedArray())
}

@Serializable
data class NotGuardDto(val guard: GuardDto) : GuardDto {
    override fun toDomain(): Guard = NotGuard(guard.toDomain())
}

@Serializable
data class EqualsGuardDto(val left: ValueReferenceDto, val right: ValueReferenceDto) : GuardDto {
    override fun toDomain(): Guard = EqualsGuard(left.toDomain(), right.toDomain())
}

@Serializable
data class NotEqualsGuardDto(val left: ValueReferenceDto, val right: ValueReferenceDto) : GuardDto {
    override fun toDomain(): Guard = NotEqualsGuard(left.toDomain(), right.toDomain())
}

@Serializable
data class GreaterThanGuardDto(val left: ValueReferenceDto, val right: ValueReferenceDto) : GuardDto {
    override fun toDomain(): Guard = GreaterThanGuard(left.toDomain(), right.toDomain())
}

@Serializable
data class LessThanGuardDto(val left: ValueReferenceDto, val right: ValueReferenceDto) : GuardDto {
    override fun toDomain(): Guard = LessThanGuard(left.toDomain(), right.toDomain())
}

@Serializable
data class GreaterThanOrEqualToGuardDto(val left: ValueReferenceDto, val right: ValueReferenceDto) : GuardDto {
    override fun toDomain(): Guard = GreaterThanOrEqualToGuard(left.toDomain(), right.toDomain())
}

@Serializable
data class LessThanOrEqualToGuardDto(val left: ValueReferenceDto, val right: ValueReferenceDto) : GuardDto {
    override fun toDomain(): Guard = LessThanOrEqualToGuard(left.toDomain(), right.toDomain())
}
