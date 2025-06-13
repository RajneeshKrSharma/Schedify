package com.unique.schedify.core.presentation.common_composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import com.unique.schedify.R
import kotlin.math.sin

@Composable
fun LoadingUi() {
    val rippleColor = MaterialTheme.colorScheme.onBackground
    val waveColor = MaterialTheme.colorScheme.onPrimaryContainer

    val infiniteTransition = rememberInfiniteTransition()

    // Wave motion animation
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(waveColor.copy(alpha = 0.1f))
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        for (i in 0..3) {
                            drawCircle(
                                color = rippleColor.copy(alpha = 0.1f),
                                radius = 400f - (i * 80) + waveOffset,
                                center = center
                            )
                        }
                    }
            )

            Text(
                text = stringResource(R.string.please_wait),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.graphicsLayer {
                    translationY = sin(waveOffset / 50) * 10
                }
            )
        }
    }
}