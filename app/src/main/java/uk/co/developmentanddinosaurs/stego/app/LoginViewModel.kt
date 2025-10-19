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

    val definition = stateDef(application.applicationContext)
    private val engine = StateMachineEngine(
        definition = definition,
        scope = CoroutineScope(viewModelScope.coroutineContext + dispatcher)
    )

    init {
        println("Hello")
        println(definition)
    }

    val uiState: StateFlow<StateMachineOutput> = engine.output

    fun onEvent(event: Event) {
        engine.send(event)
    }
}
