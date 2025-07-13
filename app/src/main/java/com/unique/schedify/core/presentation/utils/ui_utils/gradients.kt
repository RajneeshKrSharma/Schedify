package com.unique.schedify.core.presentation.utils.ui_utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import javax.inject.Singleton

object AppBaseGradients {

    @Composable
    fun baseBgGradient(): Brush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.onSecondaryContainer,
            MaterialTheme.colorScheme.primary,
        )
    )

    @Composable
    fun baseBgDarkGradient(): Brush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.onSecondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
        )
    )

    @Composable
    fun baseBtnGradient(): Brush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            MaterialTheme.colorScheme.primary,
        )
    )

    @Composable
    fun disabledBgGradient(): Brush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.inversePrimary
        )
    )

    @Composable
    fun pendingBgGradient(): Brush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.tertiary
        )
    )

    @Composable
    fun failureBgGradient(): Brush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.secondary
        )
    )

    @Composable
    fun successBgGradient(): Brush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceBright,
            MaterialTheme.colorScheme.onSecondary
        )
    )
}