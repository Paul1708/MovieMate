package de.fh.erfurt.moviemate.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

// Shared screen-level layout values used by top bars, placeholders and content padding.
// They live outside the theme tokens so screen scaffolds can depend on one stable API.
object MovieMateScreenDefaults {
    val HorizontalPadding = MovieMateSpacing.screenHorizontalPadding
    val VerticalPadding = MovieMateSpacing.screenVerticalPadding
    val TitleHeight = MovieMateSpacing.titleHeight
    val TopBarHeight = MovieMateSpacing.topBarHeight
    val DividerThickness = MovieMateSpacing.dividerThickness
    val ContentTopSpacing = MovieMateSpacing.contentTopSpacing
}

/**
 * Simple placeholder header for screens that do not yet have a custom top bar.
 *
 * Fully designed screens currently build their own top bars but reuse
 * [MovieMateScreenDefaults] for consistent spacing.
 */
@Composable
fun ScreenHeader(
    title: String,
    topBar: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MovieMateScreenDefaults.TitleHeight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MovieMateColors.PrimaryText
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MovieMateScreenDefaults.TopBarHeight),
            contentAlignment = Alignment.Center
        ) {
            topBar()
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = MovieMateScreenDefaults.DividerThickness,
            color = MovieMateColors.Divider
        )
    }
}
