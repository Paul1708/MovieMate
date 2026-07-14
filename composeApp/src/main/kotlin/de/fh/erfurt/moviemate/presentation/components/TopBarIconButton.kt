package de.fh.erfurt.moviemate.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/** Shared top-bar action with a consistent visual size and 48 dp touch target. */
@Composable
fun TopBarIconButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(MovieMateSpacing.topBarTouchTargetSize),
    ) {
        Box(
            modifier = Modifier
                .size(MovieMateSpacing.topBarIconSize)
                .background(MovieMateColors.SoftControl, CircleShape)
                .border(
                    width = MovieMateSpacing.dividerThickness,
                    color = MovieMateColors.SoftControlBorder,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            MovieMateIcon(
                icon = icon,
                tint = MovieMateColors.PrimaryText,
                contentDescription = contentDescription,
                modifier = Modifier.size(MovieMateSpacing.topBarGlyphSize),
            )
        }
    }
}
