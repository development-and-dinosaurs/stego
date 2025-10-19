package uk.co.developmentanddinosaurs.stego.statemachine.guards

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

class GuardParserTest : BehaviorSpec({

    Given("a GuardParser") {
        val context = Context().put("a", 10).put("b", 10).put("c", 5).put("d", 20)
        val event = Event("TEST_EVENT", mapOf("a" to 10, "b" to 5))

        When("evaluating simple comparison expressions") {
            Then("it should correctly evaluate an '==' expression to true") {
                val guard = GuardParser.parse("({context.a} == {event.a})")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should correctly evaluate an '==' expression to false") {
                val guard = GuardParser.parse("({context.a} == {context.c})")
                guard.evaluate(context, event) shouldBe false
            }

            Then("it should correctly evaluate a '!=' expression to true") {
                val guard = GuardParser.parse("({context.a} != {context.c})")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should correctly evaluate a '!=' expression to false") {
                val guard = GuardParser.parse("({context.a} != {context.b})")
                guard.evaluate(context, event) shouldBe false
            }

            Then("it should correctly evaluate a '<' expression to true") {
                val guard = GuardParser.parse("({context.c} < {context.a})")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should correctly evaluate a '<=' expression to true") {
                val guard = GuardParser.parse("({context.a} <= {context.b})")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should correctly evaluate a '>' expression to true") {
                val guard = GuardParser.parse("({context.d} > {context.a})")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should correctly evaluate a '>=' expression to true") {
                val guard = GuardParser.parse("({context.a} >= {context.b})")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should handle literals as operands") {
                val guard = GuardParser.parse("({context.a} == 10)")
                guard.evaluate(context, event) shouldBe true
            }
        }

        When("evaluating NOT expressions") {
            Then("it should correctly evaluate a simple NOT expression") {
                val guard = GuardParser.parse("!({context.a} == {context.c})")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should correctly evaluate a double NOT expression") {
                val guard = GuardParser.parse("!!({context.a} == {context.b})")
                guard.evaluate(context, event) shouldBe true
            }
        }

        When("evaluating composite AND/OR expressions") {
            Then("it should correctly evaluate an AND expression to true") {
                val guard = GuardParser.parse("(({context.a} == {context.b}) && ({context.c} < {context.d}))")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should correctly evaluate an AND expression to false") {
                val guard = GuardParser.parse("(({context.a} == {context.c}) && ({context.c} < {context.d}))")
                guard.evaluate(context, event) shouldBe false
            }

            Then("it should correctly evaluate an OR expression to true") {
                val guard = GuardParser.parse("(({context.a} == {context.c}) || ({context.c} < {context.d}))")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should correctly evaluate an OR expression to false") {
                val guard = GuardParser.parse("(({context.a} == {context.c}) || ({context.c} > {context.d}))")
                guard.evaluate(context, event) shouldBe false
            }
        }

        When("evaluating complex nested expressions") {
            Then("it should correctly handle AND and OR precedence") {
                // (true && true) || false -> true
                val guard = GuardParser.parse("((({context.a} == {context.b}) && ({context.c} < {context.d})) || ({context.a} == {context.c}))")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should correctly handle OR and AND precedence") {
                // true || (false && true) -> true
                val guard = GuardParser.parse("(({context.a} == {context.b}) || (({context.a} == {context.c}) && ({context.c} < {context.d})))")
                guard.evaluate(context, event) shouldBe true
            }

            Then("it should correctly handle NOT with composite expressions") {
                // ! (true || false) -> false
                val guard = GuardParser.parse("! (({context.a} == {context.b}) || ({context.a} == {context.c}))")
                guard.evaluate(context, event) shouldBe false
            }

            Then("it should parse deeply nested parentheses") {
                val guard = GuardParser.parse("(((( {context.a} == 10 ))))")
                guard.evaluate(context, event) shouldBe true
            }
        }

        When("parsing invalid expressions") {
            Then("it should throw an exception for unbalanced parentheses (missing closing)") {
                val exception = shouldThrow<IllegalArgumentException> {
                    GuardParser.parse("((a == b)")
                }
                exception.message shouldBe "Mismatched parentheses: Missing ')' in '((a == b)'"
            }

            Then("it should throw an exception for unbalanced parentheses (missing opening)") {
                val exception = shouldThrow<IllegalArgumentException> {
                    GuardParser.parse("(a == b))")
                }
                exception.message shouldBe "Mismatched parentheses: Unexpected ')' in '(a == b))'"
            }

            Then("it should throw an exception for expressions not enclosed in parentheses") {
                val exception = shouldThrow<IllegalArgumentException> {
                    GuardParser.parse("a == b")
                }
                exception.message shouldBe "All expressions must be enclosed in parentheses or be a NOT expression: 'a == b'"
            }
        }
    }
})
