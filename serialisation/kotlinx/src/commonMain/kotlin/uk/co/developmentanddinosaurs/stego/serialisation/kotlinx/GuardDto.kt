package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import kotlinx.serialization.Serializable
import uk.co.developmentanddinosaurs.stego.statemachine.*

@Serializable
sealed interface GuardDto {
    fun toDomain(): Guard
}

@Serializable
data class AndGuardDto(val guards: List<GuardDto>) : GuardDto {
    override fun toDomain(): Guard = AndGuard(guards.map { it.toDomain() })
}

@Serializable
data class OrGuardDto(val guards: List<GuardDto>) : GuardDto {
    override fun toDomain(): Guard = OrGuard(guards.map { it.toDomain() })
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
data class GreaterThanOrEqualsGuardDto(val left: ValueReferenceDto, val right: ValueReferenceDto) : GuardDto {
    override fun toDomain(): Guard = GreaterThanOrEqualsGuard(left.toDomain(), right.toDomain())
}

@Serializable
data class LessThanOrEqualsGuardDto(val left: ValueReferenceDto, val right: ValueReferenceDto) : GuardDto {
    override fun toDomain(): Guard = LessThanOrEqualsGuard(left.toDomain(), right.toDomain())
}
