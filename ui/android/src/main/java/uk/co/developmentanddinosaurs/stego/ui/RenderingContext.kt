package uk.co.developmentanddinosaurs.stego.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import uk.co.developmentanddinosaurs.stego.statemachine.Context
import uk.co.developmentanddinosaurs.stego.ui.node.FieldState
import uk.co.developmentanddinosaurs.stego.ui.node.UserInteractionHandler

/**
 * A data class that holds all the shared dependencies required for rendering UI nodes.
 * This is provided to the UI tree via a CompositionLocal.
 */
data class RenderingContext(
    val stateMachineContext: Context,
    val interactionHandler: UserInteractionHandler,
    val formFields: SnapshotStateMap<String, FieldState>,
    val onStateChange: (id: String, state: FieldState) -> Unit,
    val shakeTrigger: Int,
    val onShake: () -> Unit
)

/**
 * CompositionLocal to provide the RenderingContext down the composable tree.
 */
val LocalRenderingContext = compositionLocalOf<RenderingContext> {
    error("No RenderingContext provided")
}