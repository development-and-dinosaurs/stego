package uk.co.developmentanddinosaurs.stego.statemachine.guards

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.valueresolution.ValueProvider

class GuardLogicTest :
    BehaviorSpec({
      val context = Context()
      val event = Event("TEST_EVENT")

      Given("performComparison with valid comparable values") {
        val leftProvider = ValueProvider.resolve(10)
        val rightProvider = ValueProvider.resolve(5)

        When("the comparison is performed with a '>' operation") {
          val result =
              performComparison(leftProvider, rightProvider, context, event) { l, r -> l > r }
          Then("it should return the correct result") { result shouldBe true }
        }
      }

      Given("performComparison with a null left value") {
        val leftProvider = ValueProvider.resolve(null)
        val rightProvider = ValueProvider.resolve(5)

        When("the comparison is performed") {
          Then("it should throw an IllegalArgumentException") {
            val exception =
                shouldThrow<IllegalArgumentException> {
                  performComparison(leftProvider, rightProvider, context, event) { _, _ -> true }
                }
            exception.message shouldBe "Left value cannot be null."
          }
        }
      }

      Given("performComparison with a null right value") {
        val leftProvider = ValueProvider.resolve(5)
        val rightProvider = ValueProvider.resolve(null)

        When("the comparison is performed") {
          Then("it should throw an IllegalArgumentException") {
            val exception =
                shouldThrow<IllegalArgumentException> {
                  performComparison(leftProvider, rightProvider, context, event) { _, _ -> true }
                }
            exception.message shouldBe "Right value cannot be null."
          }
        }
      }

      Given("performComparison with non-comparable types") {
        val leftProvider = ValueProvider.resolve(10)
        val rightProvider = ValueProvider.resolve("text")

        When("the comparison is performed") {
          Then("it should throw an IllegalStateException") {
            val exception =
                shouldThrow<IllegalStateException> {
                  performComparison(leftProvider, rightProvider, context, event) { l, r -> l > r }
                }
            exception.message shouldBe "Left type 'Int' and right type 'String' are not comparable."
          }
        }
      }

      Given("performComparison with a non-comparable left value") {
        // A simple data class that does not implement Comparable
        data class NotComparable(
            val value: Int,
        )

        val leftProvider = ValueProvider.resolve(NotComparable(10))
        val rightProvider = ValueProvider.resolve(5)

        When("the comparison is performed") {
          Then("it should throw an IllegalArgumentException") {
            val exception =
                shouldThrow<IllegalArgumentException> {
                  performComparison(leftProvider, rightProvider, context, event) { l, r -> l > r }
                }
            exception.message shouldBe "Left value of type 'NotComparable' is not Comparable."
          }
        }
      }

      Given("performEqualityCheck with equal values") {
        val leftProvider = ValueProvider.resolve("test")
        val rightProvider = ValueProvider.resolve("test")

        When("the equality check is performed") {
          val result = performEqualityCheck(leftProvider, rightProvider, context, event)
          Then("it should return true") { result shouldBe true }
        }
      }

      Given("performEqualityCheck with non-equal values") {
        val leftProvider = ValueProvider.resolve("test1")
        val rightProvider = ValueProvider.resolve("test2")

        When("the equality check is performed") {
          val result = performEqualityCheck(leftProvider, rightProvider, context, event)
          Then("it should return false") { result shouldBe false }
        }
      }

      Given("performEqualityCheck with two null values") {
        val leftProvider = ValueProvider.resolve(null)
        val rightProvider = ValueProvider.resolve(null)

        When("the equality check is performed") {
          val result = performEqualityCheck(leftProvider, rightProvider, context, event)
          Then("it should return true") { result shouldBe true }
        }
      }

      Given("performEqualityCheck with a null left value") {
        val leftProvider = ValueProvider.resolve(null)
        val rightProvider = ValueProvider.resolve(5)

        When("the equality check is performed") {
          val result = performEqualityCheck(leftProvider, rightProvider, context, event)
          Then("it should return false") { result shouldBe false }
        }
      }

      Given("performEqualityCheck with a null right value") {
        val leftProvider = ValueProvider.resolve(5)
        val rightProvider = ValueProvider.resolve(null)

        When("the equality check is performed") {
          val result = performEqualityCheck(leftProvider, rightProvider, context, event)
          Then("it should return false") { result shouldBe false }
        }
      }
    })
