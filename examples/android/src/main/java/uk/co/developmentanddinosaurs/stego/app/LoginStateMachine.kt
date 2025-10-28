package uk.co.developmentanddinosaurs.stego.app

import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import uk.co.developmentanddinosaurs.stego.serialisation.ActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.AssignActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.LogActionDto
import uk.co.developmentanddinosaurs.stego.serialisation.LogicStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.StateDto
import uk.co.developmentanddinosaurs.stego.serialisation.StateMachineDefinitionDto
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.ActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.AssignActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.CompositeStateMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.InvokableDefinitionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.LogActionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.LogicStateMapper
import uk.co.developmentanddinosaurs.stego.serialisation.mappers.TransitionMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.UiStateMapper
import uk.co.developmentanddinosaurs.stego.serialisation.ui.mapper.*
import uk.co.developmentanddinosaurs.stego.serialisation.ui.node.*
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MaxLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.MinLengthValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.RequiredValidationRuleDto
import uk.co.developmentanddinosaurs.stego.serialisation.ui.validators.ValidationRuleDto
import uk.co.developmentanddinosaurs.stego.statemachine.Invokable
import uk.co.developmentanddinosaurs.stego.statemachine.InvokableResult
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineDefinition
import java.io.InputStreamReader

/**
 * A mock invokable that simulates a login network request.
 */
object LoginInvokable : Invokable {
    override suspend fun invoke(input: Map<String, Any?>): InvokableResult {
        delay(2000)
        val username = input["username"]
        println("username is $username")
        return if (username == "stego") {
            InvokableResult.Success(mapOf("loggedIn" to true))
        } else {
            InvokableResult.Failure(mapOf("error" to "Invalid username"))
        }
    }
}

fun loadLoginStateMachineDefinitionJsonString(context: android.content.Context): String {
    val inputStream = context.resources.openRawResource(R.raw.login_state_machine)
    val reader = InputStreamReader(inputStream)
    val jsonString = reader.readText()
    reader.close()
    return jsonString
}

private val json = Json {
    serializersModule = SerializersModule {
        polymorphic(StateDto::class) {
            subclass(LogicStateDto::class)
            subclass(UiStateDto::class)
        }
        polymorphic(ActionDto::class) {
            subclass(LogActionDto::class)
            subclass(AssignActionDto::class)
        }
        polymorphic(UiNodeDto::class) {
            subclass(ColumnUiNodeDto::class)
            subclass(TextFieldUiNodeDto::class)
            subclass(ButtonUiNodeDto::class)
            subclass(ProgressIndicatorUiNodeDto::class)
            subclass(LabelUiNodeDto::class)
            subclass(ImageUiNodeDto::class)
        }
        polymorphic(ButtonActionDto::class) {
            subclass(SubmitButtonActionDto::class)
            subclass(BypassValidationButtonActionDto::class)
        }
        polymorphic(ValidationRuleDto::class) {
            subclass(RequiredValidationRuleDto::class)
            subclass(MinLengthValidationRuleDto::class)
            subclass(MaxLengthValidationRuleDto::class)
        }
    }
}

fun stateDefDto(context: android.content.Context): StateMachineDefinitionDto =
    json.decodeFromString<StateMachineDefinitionDto>(loadLoginStateMachineDefinitionJsonString(context))

fun stateDef(context: android.content.Context): StateMachineDefinition {
    val invokableMapper = InvokableDefinitionMapper(mapOf("LoginInvokable" to LoginInvokable))
    val actionMapper = ActionMapper(
        mapOf(
            AssignActionDto::class to AssignActionMapper(),
            LogActionDto::class to LogActionMapper { message -> println(message) }
        )
    )
    val interactionMapper = InteractionMapper()
    val buttonActionMapper = ButtonActionMapper()
    val validationRuleMapper = ValidationRuleMapper()
    val uiNodeMapper = CompositeUiNodeMapper(
        simpleMappers = mapOf(
            LabelUiNodeDto::class to LabelUiNodeMapper(),
            ProgressIndicatorUiNodeDto::class to ProgressIndicatorUiNodeMapper(),
            TextFieldUiNodeDto::class to TextFieldUiNodeMapper(interactionMapper, validationRuleMapper),
            ButtonUiNodeDto::class to ButtonUiNodeMapper(buttonActionMapper),
            ImageUiNodeDto::class to ImageUiNodeDtoMapper()
        ),
        compositeAwareFactories = mapOf(
            ColumnUiNodeDto::class to { mapper -> ColumnUiNodeMapper(mapper) }
        )
    )

    val transitionMapper = TransitionMapper(actionMapper)
    val compositeStateMapper = CompositeStateMapper(
        mapperFactories = mapOf(
            LogicStateDto::class to { stateMapper ->
                LogicStateMapper(actionMapper, invokableMapper, transitionMapper, stateMapper)
            },
            UiStateDto::class to { stateMapper ->
                UiStateMapper(stateMapper, actionMapper, invokableMapper, transitionMapper, uiNodeMapper)
            }
        )
    )

    // Perform the mapping using the fully constructed composite mapper.
    val stateMachineDefinition = compositeStateMapper.map(stateDefDto(context))
    return stateMachineDefinition
}
