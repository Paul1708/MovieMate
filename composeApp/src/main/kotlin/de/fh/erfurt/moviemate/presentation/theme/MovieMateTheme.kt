package de.fh.erfurt.moviemate.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape

private val MovieMateColorScheme = darkColorScheme(
    background = MovieMateColors.ScreenBackground,
    onBackground = MovieMateColors.PrimaryText,
    surface = MovieMateColors.CardBackground,
    onSurface = MovieMateColors.PrimaryText,
    primary = MovieMateColors.Accent,
    onPrimary = MovieMateColors.PrimaryText,
    primaryContainer = MovieMateColors.AccentContainer,
    onPrimaryContainer = MovieMateColors.PrimaryText,
    secondary = MovieMateColors.AccentLight,
    onSecondary = MovieMateColors.ScreenBackground,
    secondaryContainer = MovieMateColors.SoftControl,
    onSecondaryContainer = MovieMateColors.PrimaryText,
    surfaceVariant = MovieMateColors.SoftControl,
    onSurfaceVariant = MovieMateColors.SecondaryText,
    outline = MovieMateColors.SoftControlBorder,
    outlineVariant = MovieMateColors.Divider,
)

private val MovieMateMaterialShapes = Shapes(
    extraSmall = RoundedCornerShape(MovieMateShapes.chipRadius),
    small = RoundedCornerShape(MovieMateShapes.controlRadius),
    medium = RoundedCornerShape(MovieMateShapes.cardRadius),
    large = RoundedCornerShape(MovieMateShapes.cardRadius),
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
        shapes = MovieMateMaterialShapes,
        content = content
    )
}
