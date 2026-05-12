package de.fh.erfurt.moviemate.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.home.MovieUiModel

/**
 * Einzelne Filmkarte in der Homescreen-Liste.
 *
 * Die Karte zeigt links das Poster und rechts die wichtigsten Filmdaten:
 * Titel, Bewertung, Kategorie, Jahr und Laufzeit. Der Klick auf die Karte
 * ist bereits vorbereitet, damit später der Detail-Screen geöffnet werden kann.
 */
@Composable
fun MovieCard(
    movie: MovieUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(126.dp)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription =
                    "Movie ${movie.title}, rating ${movie.ratingText}, category ${movie.categoryText}, year ${movie.yearText}"
            },
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = MovieMateColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterPlaceholder()

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MovieMateColors.PrimaryText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(7.dp))

                MovieMetaRow(icon = "★", text = movie.ratingText)
                MovieMetaRow(icon = "▣", text = movie.categoryText)
                MovieMetaRow(icon = "▦", text = movie.yearText)
                MovieMetaRow(icon = "◷", text = movie.runtimeText)
            }
        }
    }
}

/**
 * Platzhalter für das Filmposter.
 *
 * Diese Box steht an der Stelle, an der später das echte Poster aus `posterUri`
 * angezeigt wird. Im aktuellen UI-Schritt hilft sie, die Proportionen aus dem
 * Mockup nachzubauen.
 */
@Composable
private fun PosterPlaceholder() {
    Box(
        modifier = Modifier
            .size(width = 96.dp, height = 108.dp)
            .background(MovieMateColors.PosterPlaceholder, RoundedCornerShape(5.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "▧",
            color = Color.Black,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

/**
 * Kleine Informationszeile innerhalb einer Filmkarte.
 *
 * Sie kombiniert ein einfaches Symbol mit einem Text, zum Beispiel Stern plus
 * Bewertung oder Kalender plus Jahr. Dadurch bleiben die Metadaten der Karte
 * einheitlich ausgerichtet.
 */
@Composable
private fun MovieMetaRow(
    icon: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.SecondaryText
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
