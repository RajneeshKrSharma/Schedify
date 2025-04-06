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
    val gradientColors = listOf(
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
    )

    val progress = remember { Animatable(0.1f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = progressTime, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier) {
        val size = size.width
        val strokeWidth = 12f

        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size, 0f)
            lineTo(size, size)
            lineTo(0f, size)
            close()
        }

        val pathLength = size * 4
        val dashLength = pathLength * progress.value

        val pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength, pathLength - dashLength),
            0f
        )

        drawPath(
            path = path,
            brush = Brush.sweepGradient(gradientColors),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Butt,
                pathEffect = pathEffect
            )
        )
    }
}