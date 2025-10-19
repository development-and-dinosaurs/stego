package uk.co.developmentanddinosaurs.stego.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import uk.co.developmentanddinosaurs.stego.statemachine.Event
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineEngine
import uk.co.developmentanddinosaurs.stego.statemachine.StateMachineOutput

class LoginViewModel(application: Application,
                     dispatcher: CoroutineDispatcher = Dispatchers.Main
) : AndroidViewModel(application) {

    private val engine = StateMachineEngine(
        definition = stateDef(getApplication()),
        scope = CoroutineScope(viewModelScope.coroutineContext + dispatcher)
    )

    val uiState: StateFlow<StateMachineOutput> = engine.output

    fun onEvent(event: Event) {
        engine.send(event)
    }
}
