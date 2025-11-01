package uk.co.developmentanddinosaurs.stego.statemachine.guards

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class GuardParserTest : BehaviorSpec({
    Given("an expression containing '=='") {
        val expression = "(a == 10)"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce an EqualsGuard") {
                guard.shouldBeInstanceOf<EqualsGuard>()
            }
        }
    }

    Given("an expression containing '!='") {
        val expression = "(a != 10)"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce a NotEqualsGuard") {
                guard.shouldBeInstanceOf<NotEqualsGuard>()
            }
        }
    }

    Given("an expression containing '<'") {
        val expression = "(a < 10)"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce a LessThanGuard") {
                guard.shouldBeInstanceOf<LessThanGuard>()
            }
        }
    }

    Given("an expression containing '<='") {
        val expression = "(a <= 10)"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce a LessThanOrEqualToGuard") {
                guard.shouldBeInstanceOf<LessThanOrEqualToGuard>()
            }
        }
    }

    Given("an expression containing '>'") {
        val expression = "(a > 10)"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce a GreaterThanGuard") {
                guard.shouldBeInstanceOf<GreaterThanGuard>()
            }
        }
    }

    Given("an expression containing '>='") {
        val expression = "(a >= 10)"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce a GreaterThanOrEqualToGuard") {
                guard.shouldBeInstanceOf<GreaterThanOrEqualToGuard>()
            }
        }
    }

    Given("an expression containing a single '!'") {
        val expression = "!(a == c)"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce a NotGuard") {
                guard.shouldBeInstanceOf<NotGuard>()
            }
        }
    }

    Given("an expression containing a double '!!'") {
        val expression = "!!(a == b)"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce a NotGuard") {
                guard.shouldBeInstanceOf<NotGuard>()
            }
        }
    }

    Given("an expression containing '&&'") {
        val expression = "((a == b) && (c < d))"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce an AndGuard") {
                guard.shouldBeInstanceOf<AndGuard>()
            }
        }
    }

    Given("an expression containing '||'") {
        val expression = "((a == c) || (c < d))"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce an OrGuard") {
                guard.shouldBeInstanceOf<OrGuard>()
            }
        }
    }

    Given("a complex nested not expression") {
        val expression = "!(((a == b) && (c < d)) || (a != c))"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce a NotGuard") {
                guard.shouldBeInstanceOf<NotGuard>()
            }
        }
    }

    Given("a deeply nested equals expression") {
        val expression = "(((( a == 10 ))))"

        When("the expression is parsed") {
            val guard = GuardParser.parse(expression)
            Then("it should produce an EqualsGuard") {
                guard.shouldBeInstanceOf<EqualsGuard>()
            }
        }
    }

    Given("an expression with unbalanced parentheses (missing closing)") {
        val expression = "((a == b)"

        When("the expression is parsed") {
            Then("it should throw an IllegalArgumentException") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        GuardParser.parse(expression)
                    }
                exception.message shouldBe "Mismatched parentheses: Missing ')' in '((a == b)'"
            }
        }
    }

    Given("an expression with unbalanced parentheses (missing opening)") {
        val expression = "(a == b))"

        When("the expression is parsed") {
            Then("it should throw an IllegalArgumentException") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        GuardParser.parse(expression)
                    }
                exception.message shouldBe "Mismatched parentheses: Unexpected ')' in '(a == b))'"
            }
        }
    }

    Given("an expression not enclosed in parentheses") {
        val expression = "a == b"

        When("the expression is parsed") {
            Then("it should throw an IllegalArgumentException") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        GuardParser.parse(expression)
                    }
                exception.message shouldBe
                    "All expressions must be enclosed in parentheses or be a NOT expression: 'a == b'"
            }
        }
    }

    Given("a composite expression with a non-parenthesized sub-expression") {
        val expression = "((a == b) && c < d)"

        When("the expression is parsed") {
            Then("it should throw an IllegalArgumentException") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        GuardParser.parse(expression)
                    }
                exception.message shouldBe
                    "All expressions must be enclosed in parentheses or be a NOT expression: 'c < d'"
            }
        }
    }

    Given("an expression that is not exclusively enclosed in parentheses") {
        val expression = "(a) == (b)"

        When("the expression is parsed") {
            val exception =
                shouldThrow<IllegalArgumentException> {
                    GuardParser.parse(expression)
                }
            Then("it should throw an IllegalArgumentException") {
                exception.message shouldBe
                    "All expressions must be enclosed in parentheses or be a NOT expression: '(a) == (b)'"
            }
        }
    }

    Given("an expression that does not start with a parenthesis") {
        val expression = "a == b)"

        When("the expression is parsed") {
            val exception =
                shouldThrow<IllegalArgumentException> {
                    GuardParser.parse(expression)
                }
            Then("it should throw an IllegalArgumentException") {
                exception.message shouldBe "Mismatched parentheses: Unexpected ')' in 'a == b)'"
            }
        }
    }

    Given("an expression that does not end with a parenthesis") {
        val expression = "(a == b"

        When("the expression is parsed") {
            val exception =
                shouldThrow<IllegalArgumentException> {
                    GuardParser.parse(expression)
                }
            Then("it should throw an IllegalArgumentException") {
                exception.message shouldBe "Mismatched parentheses: Missing ')' in '(a == b'"
            }
        }
    }

    Given("an empty expression") {
        val expression = "()"

        When("the expression is parsed") {
            Then("it should throw an IllegalArgumentException") {
                val exception =
                    shouldThrow<IllegalArgumentException> {
                        GuardParser.parse(expression)
                    }
                exception.message shouldBe "Expression cannot be empty."
            }
        }
    }
})
