package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    title: String = "",
    onBackClicked: () -> Unit,
    actionLabel: String? = null,
    onActionClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MovieMateScreenDefaults.TopBarHeight),
        contentAlignment = Alignment.Center
    ) {
        TopBarIconButton(
            text = MovieMateIcons.back,
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

        if (actionLabel != null && onActionClicked != null) {
            TextButton(
                onClick = onActionClicked,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = MovieMateScreenDefaults.HorizontalPadding)
            ) {
                Text(actionLabel)
            }
        }
    }
}
