package uk.co.developmentanddinosaurs.stego.statemachine

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class InvokableTest :
    BehaviorSpec({
      val resultData = mapOf("result" to "success")

      Given("an Invokable constructed as a lambda") {
        val invokable = Invokable { _ -> InvokableResult.Success(resultData) }

        When("the invokable is invoked") {
          val actualResult = invokable.invoke(mapOf())

          Then("it should return the correct InvokableResult") {
            val expectedResult = InvokableResult.Success(resultData)
            actualResult shouldBe expectedResult
          }
        }
      }
    })
