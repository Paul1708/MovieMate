package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.components.TopBarIconButton
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons

/**
 * Kopfzeile der Detailansicht mit Zurück-Aktion und optionaler rechter Aktion.
 */
@Composable
fun MovieDetailTopBar(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "",
    onEditClicked: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MovieMateScreenDefaults.TopBarHeight),
        contentAlignment = Alignment.Center
    ) {
        TopBarIconButton(
            icon = MovieMateIcons.back,
            contentDescription = "Go back",
            onClick = onBackClicked,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = MovieMateScreenDefaults.HorizontalPadding)
        )

        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MovieMateColors.PrimaryText
            )
        }

        if (onEditClicked != null) {
            TopBarIconButton(
                icon = MovieMateIcons.edit,
                contentDescription = "Edit title",
                onClick = onEditClicked,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = MovieMateScreenDefaults.HorizontalPadding)
            )
        }
    }
}
