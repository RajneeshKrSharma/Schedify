import androidx.annotation.RawRes
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*

@Composable
fun DynamicLottieAnimation(
    @RawRes rawResId: Int,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever,
    speed: Float = 1.0f,
    isPlaying: Boolean = true
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(rawResId)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        speed = speed,
        isPlaying = isPlaying
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}
