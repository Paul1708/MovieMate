package de.fh.erfurt.moviemate.presentation.movies.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.components.TopBarIconButton
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Obere Bedienleiste des Homescreens.
 *
 * Oben liegen Titel und Aktionen, darunter die Filter-Chips aus dem Mockup.
 */
@Composable
fun HomeTopBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    onSearchClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineSmall,
                color = MovieMateColors.PrimaryText,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarIconButton(
                    text = MovieMateIcons.search,
                    contentDescription = "Open search",
                    onClick = onSearchClicked
                )
                TopBarIconButton(
                    text = MovieMateIcons.filter,
                    contentDescription = "Open filter",
                    onClick = onFilterClicked
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = MovieMateSpacing.sectionSpacing),
            horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTab.entries.forEach { tab ->
                HomeTabChip(
                    tab = tab,
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun HomeTabChip(
    tab: HomeTab,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(MovieMateShapes.chipRadius)
    Box(
        modifier = Modifier
            .background(
                color = if (selected) MovieMateColors.Accent else MovieMateColors.SoftControl,
                shape = shape
            )
            .border(
                width = MovieMateSpacing.dividerThickness,
                color = if (selected) MovieMateColors.AccentLight else MovieMateColors.SoftControlBorder,
                shape = shape
            )
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Tab ${tab.label}" }
            .padding(
                horizontal = MovieMateSpacing.homeTabHorizontalPadding,
                vertical = MovieMateSpacing.homeTabVerticalPadding
            )
    ) {
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MovieMateColors.PrimaryText else MovieMateColors.SecondaryText,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
