package uk.co.developmentanddinosaurs.stego.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.co.developmentanddinosaurs.stego.app.ui.theme.StegoTheme
import uk.co.developmentanddinosaurs.stego.ui.Render
import uk.co.developmentanddinosaurs.stego.ui.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StegoTheme {
                val factory = object : ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return LoginViewModel(application = application) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
                val viewModel: LoginViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsState()
                (uiState.state as? UiState)?.let { state ->
                    Render(state.uiNode, uiState.context, viewModel::onEvent)
                }
            }
        }
    }
}
