package uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper

import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto
import uk.co.developmentanddinosaurs.stego.ui.validators.ValidationRule
import kotlin.reflect.KClass

/**
 * A [ValidationRuleMapper] that holds a map of other mappers.
 * It looks up the correct mapper based on the DTO's class and delegates the mapping task to it.
 */
class CompositeValidationRuleMapper(
    private val mappers: Map<KClass<out ValidationRuleDto>, ValidationRuleMapper>,
) : ValidationRuleMapper {
    override fun map(dto: ValidationRuleDto): ValidationRule {
        val mapper =
            mappers[dto::class]
                ?: throw IllegalArgumentException("Unsupported ValidationRuleDto type: ${dto::class.simpleName}")
        return mapper.map(dto)
    }
}
