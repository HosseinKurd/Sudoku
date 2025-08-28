package com.offlineganes.sudoku.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = RedLightPrimary,
    onPrimary = RedLightOnPrimary,
    primaryContainer = RedLightPrimaryContainer,
    onPrimaryContainer = RedLightOnPrimaryContainer,
    secondary = RedLightSecondary,
    onSecondary = RedLightOnSecondary,
    secondaryContainer = RedLightSecondaryContainer,
    onSecondaryContainer = RedLightOnSecondaryContainer,
    tertiary = RedLightTertiary,
    onTertiary = RedLightOnTertiary,
    tertiaryContainer = RedLightTertiaryContainer,
    onTertiaryContainer = RedLightOnTertiaryContainer,
    error = RedLightError,
    onError = RedLightOnError,
    background = WhiteBackground,
    onBackground = WhiteOnBackground,
    surface = WhiteSurface,
    onSurface = WhiteOnSurface,
    surfaceVariant = RedLightPrimaryContainer.copy(alpha = 0.2f), // A subtle variant for uneditable cells
    onSurfaceVariant = WhiteOnBackground // Text on surface variant
)

private val DarkColorScheme = darkColorScheme(
    primary = RedDarkPrimary,
    onPrimary = RedDarkOnPrimary,
    primaryContainer = RedDarkPrimaryContainer,
    onPrimaryContainer = RedDarkOnPrimaryContainer,
    secondary = RedDarkSecondary,
    onSecondary = RedDarkOnSecondary,
    secondaryContainer = RedDarkSecondaryContainer,
    onSecondaryContainer = RedDarkOnSecondaryContainer,
    tertiary = RedDarkTertiary,
    onTertiary = RedDarkOnTertiary,
    tertiaryContainer = RedDarkTertiaryContainer,
    onTertiaryContainer = RedDarkOnTertiaryContainer,
    error = RedDarkError,
    onError = RedDarkOnError,
    background = RedDarkBackground,
    onBackground = RedDarkOnBackground,
    surface = RedDarkSurface,
    onSurface = RedDarkOnSurface,
    surfaceVariant = RedDarkPrimaryContainer.copy(alpha = 0.4f), // A subtle variant for uneditable cells
    onSurfaceVariant = RedDarkOnBackground // Text on surface variant
)

@Composable
fun SudokuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming you have a Typography object
        content = content
    )
}