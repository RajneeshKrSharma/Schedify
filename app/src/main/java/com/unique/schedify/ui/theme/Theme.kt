package com.unique.schedify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DeepGreen,
    onPrimary = DeepPink,
    secondary = DeepRed,
    onSecondary = ShineGreen,
    tertiary = MediumOrange,
    onTertiary = DisabledBg,
    onBackground = DeepBlue,
    onPrimaryContainer = Black,
    onSecondaryContainer = White,
    surfaceContainerLow = Transparent,
)

private val LightColorScheme = lightColorScheme(
    primary = DeepGreen,
    onPrimary = DeepPink,
    secondary = DeepRed,
    onSecondary = ShineGreen,
    tertiary = MediumOrange,
    onTertiary = DisabledBg,
    onBackground = DeepBlue,
    onPrimaryContainer = Black,
    onSecondaryContainer = White,
    surfaceContainerLow = Transparent
)

@Composable
fun SchedifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }*/

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}