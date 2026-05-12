package de.fh.erfurt.moviemate.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.MovieMateColors

/**
 * Obere Bedienleiste des Homescreens.
 *
 * Hier wählt der Nutzer zwischen allen Filmen, gesehenen Filmen und der
 * Watch-later-Ansicht. Rechts liegen getrennte Aktionen für Suche, Filter und
 * Sortierung.
 */
@Composable
fun HomeTopBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    onSearchClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onSortClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTab.entries.forEach { tab ->
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MovieMateColors.PrimaryText,
                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .semantics { contentDescription = "Tab ${tab.label}" }
                        .padding(vertical = 10.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopBarIconButton(
                text = "⌕",
                contentDescription = "Suche öffnen",
                onClick = onSearchClicked
            )
            TopBarIconButton(
                text = "▾",
                contentDescription = "Filter öffnen",
                onClick = onFilterClicked
            )
            TopBarIconButton(
                text = "↕",
                contentDescription = "Sortierung öffnen",
                onClick = onSortClicked
            )
        }
    }
}

/**
 * Kleine Icon-Schaltfläche für die rechte Seite der Topbar.
 *
 * Aktuell werden einfache Textsymbole genutzt. Später können hier echte Icons
 * eingesetzt werden, ohne die Topbar-Struktur zu ändern.
 */
@Composable
private fun TopBarIconButton(
    text: String,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(MovieMateColors.SoftControl, CircleShape)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = MovieMateColors.SecondaryText
        )
    }
}
