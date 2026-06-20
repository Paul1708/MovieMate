package de.fh.erfurt.moviemate.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

/**
 * Dunkler Screen-Hintergrund
 */
@Composable
fun MovieMateScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MovieMateColors.ScreenBackgroundTop,
                        MovieMateColors.ScreenBackground,
                        MovieMateColors.ScreenBackground
                    )
                )
            ),
        content = content
    )
}
