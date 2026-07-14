package de.fh.erfurt.moviemate.presentation.movies.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons

@Composable
internal fun MovieRuntimeIcon(modifier: Modifier = Modifier) {
    MovieMateIcon(
        icon = MovieMateIcons.runtime,
        tint = MovieMateColors.AccentLight,
        modifier = modifier,
    )
}

@Composable
internal fun MovieDateIcon(modifier: Modifier = Modifier) {
    MovieMateIcon(
        icon = MovieMateIcons.calendar,
        tint = MovieMateColors.AccentLight,
        modifier = modifier,
    )
}
