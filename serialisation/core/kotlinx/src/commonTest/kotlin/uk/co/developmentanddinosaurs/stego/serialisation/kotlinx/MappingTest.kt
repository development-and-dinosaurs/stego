package uk.co.developmentanddinosaurs.stego.serialisation.kotlinx

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.statemachine.ContextReference
import uk.co.developmentanddinosaurs.stego.statemachine.EqualsGuard
import uk.co.developmentanddinosaurs.stego.statemachine.LiteralReference
import uk.co.developmentanddinosaurs.stego.statemachine.LongPrimitive

class MappingTest : StringSpec({

    "Can map a complex DTO to a domain object" {
        // 1. Construct the DTO in code
        val definitionDto = StateMachineDefinitionDto(
            initial = "Idle",
            initialContext = mapOf(
                "userId" to LongPrimitiveDto(123)
            ),
            states = mapOf(
                "Idle" to LogicStateDto(
                    id = "Idle",
                    on = mapOf(
                        "START" to listOf(
                            TransitionDto(
                                target = "Active",
                                guard = EqualsGuardDto(
                                    left = ContextReferenceDto("userId"),
                                    right = LiteralReferenceDto(LongPrimitiveDto(123))
                                )
                            )
                        )
                    )
                ),
                "Active" to LogicStateDto(id = "Active")
            )
        )

        // 2. Call the toDomain() mapping function
        val definition = definitionDto.toDomain()

        // 3. Assert on the resulting domain object
        definition.initial shouldBe "Idle"
        (definition.initialContext.get("userId") as? LongPrimitive)?.value shouldBe 123L

        val idleState = definition.states["Idle"]
        val transition = idleState?.on?.get("START")?.first()
        transition?.target shouldBe "Active"

        val guard = transition?.guard
        guard.shouldBeInstanceOf<EqualsGuard>()
        guard.left.shouldBeInstanceOf<ContextReference>()
        guard.right.shouldBeInstanceOf<LiteralReference>()
        ((guard.right as LiteralReference).value as LongPrimitive).value shouldBe 123L
    }
})
