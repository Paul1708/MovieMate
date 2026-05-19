package de.fh.erfurt.moviemate.presentation

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
import androidx.compose.ui.unit.dp

// Gemeinsame Maße für die oberen Bereiche aller Screens.
// Dadurch sitzen Titel, Topbar-Icons und Divider beim Screenwechsel gleich.
object ScreenHeaderDefaults {
    val HorizontalPadding = 18.dp
    val VerticalPadding = 18.dp
    val TitleHeight = 44.dp
    val TopBarHeight = 44.dp
    val DividerThickness = 2.dp
    val ContentTopSpacing = 18.dp
}

/**
 * Einheitlicher oberer Screen-Bereich.
 *
 * Diese Composable legt die Höhe des Titels, der Topbar und des Dividers fest.
 * Home und Kategorien nutzen sie gemeinsam, damit die Elemente beim Wechsel
 * nicht optisch springen.
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
                .height(ScreenHeaderDefaults.TitleHeight),
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
                .height(ScreenHeaderDefaults.TopBarHeight),
            contentAlignment = Alignment.Center
        ) {
            topBar()
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = ScreenHeaderDefaults.DividerThickness,
            color = MovieMateColors.Divider
        )
    }
}
