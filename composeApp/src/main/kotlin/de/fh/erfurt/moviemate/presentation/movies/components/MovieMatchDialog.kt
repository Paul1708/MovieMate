package de.fh.erfurt.moviemate.presentation.movies.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieSearchResults
import de.fh.erfurt.moviemate.presentation.movies.detail.MovieDetailPoster

@Composable
internal fun MovieMatchDialog(
    results: MovieSearchResults,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose a match") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (results.totalResults > results.matches.size) {
                    Text("Showing the first ${results.matches.size} of ${results.totalResults} matches. Add a year or refine the title to narrow the search.")
                }
                LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                    items(results.matches, key = { it.imdbId }) { movie ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable(role = Role.Button) { onSelect(movie.imdbId) }
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            MovieDetailPoster(
                                posterUrl = movie.posterUrl,
                                modifier = Modifier.size(width = 42.dp, height = 62.dp),
                            )
                            Column {
                                Text(movie.title, style = MaterialTheme.typography.titleSmall)
                                if (movie.year.isNotBlank()) {
                                    Text(movie.year, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
