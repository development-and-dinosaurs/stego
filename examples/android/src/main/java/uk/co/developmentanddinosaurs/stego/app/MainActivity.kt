package uk.co.developmentanddinosaurs.stego.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import uk.co.developmentanddinosaurs.stego.app.ui.theme.StegoTheme
import uk.co.developmentanddinosaurs.stego.ui.Render
import uk.co.developmentanddinosaurs.stego.ui.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StegoTheme {
                val viewModel: LoginViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsState()

                (uiState.state as? UiState)?.let { state ->
                    Render(state.uiNode, uiState.context, viewModel::onEvent)
                }
            }
        }
    }
}
