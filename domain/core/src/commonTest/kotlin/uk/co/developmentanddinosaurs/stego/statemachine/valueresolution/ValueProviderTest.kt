package uk.co.developmentanddinosaurs.stego.statemachine.valueresolution

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event

class ValueProviderTest : BehaviorSpec() {
  init {
    val context = Context().put("myKey", 123)
    val event = Event("TEST", mapOf("myKey" to "abc"))

    Given("a context value expression") {
      val expression = "{context.myKey}"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a ContextValue provider that returns the correct value") {
          provider.shouldBeInstanceOf<ContextValue>()
          provider.get(context, event) shouldBe 123
        }
      }
    }

    Given("an event value expression") {
      val expression = "{event.myKey}"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create an EventValue provider that returns the correct value") {
          provider.shouldBeInstanceOf<EventValue>()
          provider.get(context, event) shouldBe "abc"
        }
      }
    }

    Given("an integer literal string") {
      val expression = "42"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a LiteralValue provider with the correct integer") {
          provider.shouldBeInstanceOf<LiteralValue>()
          provider.get(context, event) shouldBe 42
        }
      }
    }

    Given("a double literal string") {
      val expression = "3.14"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a LiteralValue provider with the correct double") {
          provider.shouldBeInstanceOf<LiteralValue>()
          provider.get(context, event) shouldBe 3.14
        }
      }
    }

    Given("a boolean 'true' literal string") {
      val expression = "true"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a LiteralValue provider with the correct boolean") {
          provider.shouldBeInstanceOf<LiteralValue>()
          provider.get(context, event) shouldBe true
        }
      }
    }

    Given("a boolean 'false' literal string") {
      val expression = "false"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a LiteralValue provider with the correct boolean") {
          provider.shouldBeInstanceOf<LiteralValue>()
          provider.get(context, event) shouldBe false
        }
      }
    }

    Given("a quoted string literal") {
      val expression = "\"hello world\""

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a LiteralValue provider with the unquoted string") {
          provider.shouldBeInstanceOf<LiteralValue>()
          provider.get(context, event) shouldBe "hello world"
        }
      }
    }

    Given("an unquoted string literal") {
      val expression = "a_simple_string"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a LiteralValue provider with the string as-is") {
          provider.shouldBeInstanceOf<LiteralValue>()
          provider.get(context, event) shouldBe "a_simple_string"
        }
      }
    }

    Given("a context value expression for a non-existent key") {
      val expression = "{context.missingKey}"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a ContextValue provider that returns null") {
          provider.shouldBeInstanceOf<ContextValue>()
          provider.get(context, event) shouldBe null
        }
      }
    }

    Given("an event value expression for a non-existent key") {
      val expression = "{event.missingKey}"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create an EventValue provider that returns null") {
          provider.shouldBeInstanceOf<EventValue>()
          provider.get(context, event) shouldBe null
        }
      }
    }

    Given("a string literal that starts with a quote but doesn't end with one") {
      val expression = "\"hello"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a LiteralValue provider with the string as-is") {
          provider.shouldBeInstanceOf<LiteralValue>()
          provider.get(context, event) shouldBe "\"hello"
        }
      }
    }

    Given("a string literal that ends with a quote but doesn't start with one") {
      val expression = "world\""

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a LiteralValue provider with the string as-is") {
          provider.shouldBeInstanceOf<LiteralValue>()
          provider.get(context, event) shouldBe "world\""
        }
      }
    }

    Given("a string literal with quotes in the middle but not at the start/end") {
      val expression = "hello\"world"

      When("the expression is resolved") {
        val provider = ValueProvider.resolve(expression)
        Then("it should create a LiteralValue provider with the string as-is") {
          provider.shouldBeInstanceOf<LiteralValue>()
          provider.get(context, event) shouldBe "hello\"world"
        }
      }
    }
  }
}
