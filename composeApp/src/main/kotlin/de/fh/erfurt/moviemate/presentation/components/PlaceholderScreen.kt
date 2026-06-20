package de.fh.erfurt.moviemate.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

/**
 * Einheitlicher Platzhalter für noch nicht gebaute Screens.
 */
@Composable
fun PlaceholderScreen(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    MovieMateScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = MovieMateScreenDefaults.HorizontalPadding,
                    vertical = MovieMateScreenDefaults.VerticalPadding
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenHeader(
                title = title,
                topBar = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = MovieMateScreenDefaults.ContentTopSpacing),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MovieMateColors.SecondaryText
                )
            }
        }
    }
}
