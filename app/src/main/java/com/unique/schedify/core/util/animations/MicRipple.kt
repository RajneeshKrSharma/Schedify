package com.unique.schedify.core.util.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MicRipple(
    isSpeaking: Boolean,
    color: Color,
    maxRadius: Dp = 24.dp
) {
    val radius = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(isSpeaking) {
        if (isSpeaking) {
            // Start infinite ripple using repeated animation
            while (true) {
                radius.snapTo(0f)
                alpha.snapTo(0.4f)

                launch {
                    radius.animateTo(
                        targetValue = maxRadius.value,
                        animationSpec = tween(durationMillis = 800)
                    )
                }
                launch {
                    alpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 800)
                    )
                }

                delay(800)
            }
        } else {
            // Reset immediately when speaking stops
            radius.snapTo(0f)
            alpha.snapTo(0f)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (alpha.value > 0f) {
            drawCircle(
                color = color,
                radius = radius.value * density,
                center = center,
                alpha = alpha.value
            )
        }
    }
}
