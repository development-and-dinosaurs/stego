package uk.co.developmentanddinosaurs.stego.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineEngine
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineOutput

class LoginViewModel(
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val engine = StateMachineEngine(
        definition = loginStateMachineDefinition,
        scope = CoroutineScope(viewModelScope.coroutineContext + dispatcher)
    )

    val uiState: StateFlow<StateMachineOutput> = engine.output

    fun onEvent(event: Event) {
        engine.send(event)
    }
}
