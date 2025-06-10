package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.unique.schedify.core.presentation.utils.size_units.sp10
import kotlinx.coroutines.delay

@Composable
fun ImageWithLoadingIndicator(
    imageUrl: String,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .crossfade(true)
        .build()

    val painter = rememberAsyncImagePainter(model = request)

    var progress by remember { mutableFloatStateOf(0f) }
    var showLoader by remember { mutableStateOf(true) }

    // Listen to painter state change
    LaunchedEffect(painter.state) {
        when (painter.state) {
            is AsyncImagePainter.State.Success -> {
                progress = 100f
                showLoader = false
            }

            is AsyncImagePainter.State.Error -> {
                showLoader = false
            }

            else -> {
                showLoader = true
            }
        }
    }

    // Animate progress only while loading
    LaunchedEffect(showLoader) {
        while (showLoader && progress < 100f) {
            delay(40)
            // Only increase if still loading and not yet 100
            if (progress < 95f && painter.state is AsyncImagePainter.State.Loading) {
                progress = (progress + 5f).coerceAtMost(95f)
            }
        }
    }

    val displayProgress = progress.coerceIn(0f, 100f)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = imageModifier,
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )

        if (showLoader) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { displayProgress / 100f },
                    trackColor = ProgressIndicatorDefaults.circularDeterminateTrackColor,
                )
                Text(
                    text = "${displayProgress.toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = sp10
                    ),
                )
            }
        }
    }
}