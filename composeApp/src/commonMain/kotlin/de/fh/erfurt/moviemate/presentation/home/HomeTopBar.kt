package de.fh.erfurt.moviemate.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Obere Bedienleiste des Homescreens.
 *
 * Hier wählt der Nutzer zwischen allen Filmen, gesehenen Filmen und der
 * Watch-later-Ansicht. Rechts liegen die Aktionen für Suche und Filter,
 * passend zur oberen Zeile im Mockup.
 */
@Composable
fun HomeTopBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    onSearchClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HomeTab.entries.forEach { tab ->
            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .clickable { onTabSelected(tab) }
                    .semantics { contentDescription = "Tab ${tab.label}" }
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⌕",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .clickable(onClick = onSearchClicked)
                    .semantics { contentDescription = "Suche öffnen" }
                    .padding(4.dp)
            )
            Text(
                text = "▾",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .clickable(onClick = onFilterClicked)
                    .semantics { contentDescription = "Filter öffnen" }
                    .padding(4.dp)
            )
        }
    }
}
