package de.fh.erfurt.moviemate.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MovieMateColorScheme = darkColorScheme(
    background = MovieMateColors.ScreenBackground,
    surface = MovieMateColors.CardBackground,
    onSurface = MovieMateColors.PrimaryText,
    primary = MovieMateColors.Accent,
    onPrimary = MovieMateColors.PrimaryText,
    secondary = MovieMateColors.SecondaryText,
    surfaceVariant = MovieMateColors.SoftControl,
    outline = MovieMateColors.Divider
)

/**
 * Zentrales Theme der App, damit Farben später an einer Stelle angepasst werden können.
 */
@Composable
fun MovieMateTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MovieMateColorScheme,
        content = content
    )
}
