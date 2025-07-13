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
    var isImageDone by remember { mutableStateOf(false) }

    // Respond to painter state
    LaunchedEffect(painter.state) {
        when (painter.state) {
            is AsyncImagePainter.State.Success,
            is AsyncImagePainter.State.Error -> {
                isImageDone = true
            }
            else -> {
                isImageDone = false
            }
        }
    }

    // Drive the progress continuously until 100
    LaunchedEffect(isImageDone) {
        while (!isImageDone && progress < 99f) {
            delay(30)
            progress = (progress + 1f).coerceAtMost(99f)
        }

        // Once image is done loading, finish progress to 100
        if (isImageDone) {
            progress = 100f
            delay(300) // Optional: slight delay for smooth disappearance
            showLoader = false
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
                    trackColor = MaterialTheme.colorScheme.surfaceContainerLow
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