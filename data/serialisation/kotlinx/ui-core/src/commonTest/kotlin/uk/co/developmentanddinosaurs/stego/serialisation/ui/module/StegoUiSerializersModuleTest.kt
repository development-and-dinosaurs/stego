package uk.co.developmentanddinosaurs.stego.serialisation.ui.module

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.Json
import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ButtonUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.BypassValidationButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ColumnUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.GridUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ImageUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.LabelUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.ProgressIndicatorUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.SubmitButtonActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.TextFieldUiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.UiNodeDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MaxLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto

class StegoUiSerializersModuleTest : BehaviorSpec() {
  init {
    val json = Json {
      serializersModule = stegoUiSerializersModule
      classDiscriminator = "type"
      ignoreUnknownKeys = true
    }

    Given("a Json instance with stegoUiSerializersModule") {
      When("deserializing a UiStateDto") {
        val stateJson =
            """
            {
              "id": "state",
              "type": "ui",
              "uiNode": {
                "type": "stego.label",
                "id": "a",
                "text": "b"
              }
            }
            """
                .trimIndent()
        val stateDto = json.decodeFromString<StateDto>(stateJson)

        Then("it should deserialize correctly") { stateDto.shouldBeInstanceOf<UiStateDto>() }
      }

      When("deserializing a ColumnUiNodeDto") {
        val nodeJson =
            """
            {
              "type": "stego.column",
              "id": "a",
              "children": []
            }
            """
                .trimIndent()
        val nodeDto = json.decodeFromString<UiNodeDto>(nodeJson)
        Then("it should deserialize correctly") { nodeDto.shouldBeInstanceOf<ColumnUiNodeDto>() }
      }

      When("deserializing a TextFieldUiNodeDto") {
        val nodeJson =
            """
            {
              "type": "stego.text_field",
              "id": "a",
              "text": "b",
              "label": "c",
              "onTextChanged": {
                "trigger": "d"
              },
              "validators": []
            }
            """
                .trimIndent()
        val nodeDto = json.decodeFromString<UiNodeDto>(nodeJson)
        Then("it should deserialize correctly") { nodeDto.shouldBeInstanceOf<TextFieldUiNodeDto>() }
      }

      When("deserializing a ButtonUiNodeDto") {
        val nodeJson =
            """
            {
              "type": "stego.button",
              "id": "a",
              "text": "b",
              "onClick": {
                "type": "bypass_validation",
                "trigger": "c"
              }
            }
            """
                .trimIndent()
        val nodeDto = json.decodeFromString<UiNodeDto>(nodeJson)
        Then("it should deserialize correctly") { nodeDto.shouldBeInstanceOf<ButtonUiNodeDto>() }
      }

      When("deserializing a ProgressIndicatorUiNodeDto") {
        val nodeJson =
            """
            {
              "type": "stego.progress_indicator",
              "id": "a"
            }
            """
                .trimIndent()
        val nodeDto = json.decodeFromString<UiNodeDto>(nodeJson)
        Then("it should deserialize correctly") {
          nodeDto.shouldBeInstanceOf<ProgressIndicatorUiNodeDto>()
        }
      }

      When("deserializing a LabelUiNodeDto") {
        val nodeJson =
            """
            {
              "type": "stego.label",
              "id": "a",
              "text": "b"
            }
            """
                .trimIndent()
        val nodeDto = json.decodeFromString<UiNodeDto>(nodeJson)
        Then("it should deserialize correctly") { nodeDto.shouldBeInstanceOf<LabelUiNodeDto>() }
      }

      When("deserializing an ImageUiNodeDto") {
        val nodeJson =
            """
            {
              "type": "stego.image",
              "id": "a",
              "url": "b",
              "contentDescription": "c"
            }
            """
                .trimIndent()
        val nodeDto = json.decodeFromString<UiNodeDto>(nodeJson)
        Then("it should deserialize correctly") { nodeDto.shouldBeInstanceOf<ImageUiNodeDto>() }
      }

      When("deserializing a GridUiNodeDto") {
        val nodeJson =
            """
            {
              "type": "stego.grid",
              "id": "a",
              "columns": 1,
              "children": []
            }
            """
                .trimIndent()
        val nodeDto = json.decodeFromString<UiNodeDto>(nodeJson)
        Then("it should deserialize correctly") { nodeDto.shouldBeInstanceOf<GridUiNodeDto>() }
      }

      When("deserializing a SubmitButtonActionDto") {
        val actionJson =
            """
            {
              "type": "submit",
              "trigger": "a",
              "validationScope": ["b"],
              "onValidationFail": "c"
            }
            """
                .trimIndent()
        val actionDto = json.decodeFromString<ButtonActionDto>(actionJson)

        Then("it should deserialize correctly") {
          actionDto.shouldBeInstanceOf<SubmitButtonActionDto>()
          actionDto.trigger shouldBe "a"
          actionDto.validationScope shouldBe listOf("b")
          actionDto.onValidationFail shouldBe "c"
        }
      }

      When("deserializing a BypassValidationButtonActionDto") {
        val actionJson =
            """
            {
              "type": "bypass_validation",
              "trigger": "a"
            }
            """
                .trimIndent()
        val actionDto = json.decodeFromString<ButtonActionDto>(actionJson)

        Then("it should deserialize correctly") {
          actionDto.shouldBeInstanceOf<BypassValidationButtonActionDto>()
          actionDto.trigger shouldBe "a"
        }
      }

      When("deserializing a RequiredValidationRuleDto") {
        val validationJson =
            """
            {
              "type": "required",
              "message": "a"
            }
            """
                .trimIndent()
        val validationDto = json.decodeFromString<ValidationRuleDto>(validationJson)

        Then("it should deserialize correctly") {
          validationDto.shouldBeInstanceOf<RequiredValidationRuleDto>()
          validationDto.message shouldBe "a"
        }
      }

      When("deserializing a MinLengthValidationRuleDto") {
        val validationJson =
            """
            {
              "type": "minLength",
              "message": "a",
              "length": 1
            }
            """
                .trimIndent()
        val validationDto = json.decodeFromString<ValidationRuleDto>(validationJson)

        Then("it should deserialize correctly") {
          validationDto.shouldBeInstanceOf<MinLengthValidationRuleDto>()
          validationDto.message shouldBe "a"
          validationDto.length shouldBe 1
        }
      }

      When("deserializing a MaxLengthValidationRuleDto") {
        val validationJson =
            """
            {
              "type": "maxLength",
              "message": "a",
              "length": 1
            }
            """
                .trimIndent()
        val validationDto = json.decodeFromString<ValidationRuleDto>(validationJson)

        Then("it should deserialize correctly") {
          validationDto.shouldBeInstanceOf<MaxLengthValidationRuleDto>()
          validationDto.message shouldBe "a"
          validationDto.length shouldBe 1
        }
      }
    }
  }
}
