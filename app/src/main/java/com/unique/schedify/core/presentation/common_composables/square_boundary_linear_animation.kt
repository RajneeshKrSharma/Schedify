package com.unique.schedify.core.presentation.common_composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SquareBoundaryProgressBar(
    modifier: Modifier = Modifier,
    progressTime: Int
) {
    val color = MaterialTheme.colorScheme.secondary

    val progress = remember { Animatable(0.0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = progressTime, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier) {
        val canvasSize = size.minDimension
        val strokeWidth = 12f

        val path = Path().apply {
            moveTo(0f, 0f)                    // Top-left
            lineTo(canvasSize, 0f)           // Top-right
            lineTo(canvasSize, canvasSize)   // Bottom-right
            lineTo(0f, canvasSize)           // Bottom-left
            close()                          // Back to top-left
        }

        val totalLength = 4 * canvasSize
        val animatedLength = totalLength * progress.value

        val pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(animatedLength, totalLength),
            0f // phase
        )

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth,
                pathEffect = pathEffect,
                cap = StrokeCap.Butt
            )
        )
    }
}
