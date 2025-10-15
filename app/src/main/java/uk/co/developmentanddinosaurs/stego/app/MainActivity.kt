package uk.co.developmentanddinosaurs.stego.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.co.developmentanddinosaurs.stego.ui.Render
import uk.co.developmentanddinosaurs.stego.ui.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LoginViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            (uiState.state as? UiState)?.let { state ->
                Render(state.view, uiState.context, viewModel::onEvent)
            }
        }
    }
}
