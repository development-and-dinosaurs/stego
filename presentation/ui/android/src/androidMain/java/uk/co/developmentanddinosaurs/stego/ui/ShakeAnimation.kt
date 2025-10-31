package uk.co.developmentanddinosaurs.stego.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

private const val SHAKE_DISTANCE = 10f
private const val SHAKE_DURATION_MS = 50
private const val SHAKE_REPETITIONS = 2

fun Modifier.shake(
    shakeTrigger: Int,
    onAnimationFinished: () -> Unit = {},
) = composed {
    val shake = remember { Animatable(0f) }
    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger > 0) {
            shake.animateTo(
                targetValue = SHAKE_DISTANCE,
                animationSpec = tween(durationMillis = SHAKE_DURATION_MS, delayMillis = 0),
            )
            repeat(SHAKE_REPETITIONS) {
                shake.animateTo(-SHAKE_DISTANCE, animationSpec = tween(durationMillis = SHAKE_DURATION_MS))
                shake.animateTo(SHAKE_DISTANCE, animationSpec = tween(durationMillis = SHAKE_DURATION_MS))
            }
            shake.animateTo(0f, animationSpec = tween(durationMillis = SHAKE_DURATION_MS))
            onAnimationFinished()
        }
    }
    this.graphicsLayer {
        translationX = shake.value
    }
}
