package com.unique.schedify.core.presentation.common_composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unique.schedify.R
import com.unique.schedify.core.presentation.utils.size_units.dp16
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LoadingUi() {
    val backgroundColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
    val dotColor = MaterialTheme.colorScheme.onSecondaryContainer
    val textColor = MaterialTheme.colorScheme.onSecondaryContainer
    val colors = listOf(
        MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f), )

    val infiniteTransition = rememberInfiniteTransition(label = "loading_animation")

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(5000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    val dotsRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(5000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "dots_rotation"
    )

    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(dp16),
            contentAlignment = Alignment.Center
        ) {
            // Rotating Ring
            Canvas(modifier = Modifier.size(180.dp)) {
                rotate(degrees = ringRotation) {
                    val strokeWidth = 8f
                    val radius = size.minDimension / 2
                    val segmentCount = 36
                    val fullSweep = 360f / segmentCount
                    val gapAngle = 4f // Degrees of gap between segments
                    val arcSweep = fullSweep - gapAngle // Actual sweep of arc

                    for (i in 0 until segmentCount) {
                        drawArc(
                            color = colors[i % colors.size],
                            startAngle = i * fullSweep,
                            sweepAngle = arcSweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth),
                            size = Size(radius * 2, radius * 2),
                            topLeft = Offset(
                                (size.width - radius * 2) / 2f,
                                (size.height - radius * 2) / 2f
                            )
                        )
                    }
                }
            }


            // Rotating Dots
            Canvas(modifier = Modifier.size(180.dp)) {
                val radius = size.minDimension / 2.5f
                val dotCount = 8
                val angleStep = 360f / dotCount

                for (i in 0 until dotCount) {
                    val angle = (i * angleStep + dotsRotation) * (PI / 180).toFloat()
                    val dotOffset = Offset(
                        x = center.x + radius * cos(angle),
                        y = center.y + radius * sin(angle)
                    )
                    drawCircle(color = dotColor, radius = 5f, center = dotOffset)
                }
            }


            // ✨ Shimmering Text (inside the ring)
            val gradient = Brush.linearGradient(
                colors = listOf(
                    textColor.copy(alpha = 0.7f),
                    textColor.copy(alpha = 1f),
                    textColor.copy(alpha = 0.7f),
                ),
                start = Offset.Zero,
                end = Offset(x = 500f * shimmerTranslate, y = 0f)
            )

            Text(
                text = stringResource(R.string.please_wait),
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = gradient,
                    textAlign = TextAlign.Center
                )
            )
        }
    }

}