package de.fh.erfurt.moviemate.presentation.movies.categories

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon

/** Renders the locally bundled Material Symbol for a category. */
@Composable
internal fun CategoryGenreIcon(
    iconKey: String?,
    modifier: Modifier = Modifier,
) {
    MovieMateIcon(
        icon = iconKey.categoryIconOption().icon,
        tint = MovieMateColors.AccentLight,
        modifier = modifier,
        contentDescription = null,
    )
}
