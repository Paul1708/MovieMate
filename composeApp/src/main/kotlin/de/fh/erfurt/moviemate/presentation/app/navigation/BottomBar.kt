package de.fh.erfurt.moviemate.presentation.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Untere Navigation der App.
 *
 * Die Bar zeigt die Hauptbereiche aus dem Mockup: Home, Kategorien, Add und
 * Backup. Die Klicks sind schon als Callback vorbereitet, werden aber erst
 * später an eine echte Navigation angeschlossen.
 */
@Composable
fun BottomBar(
    selectedItem: BottomNavItem = BottomNavItem.Home,
    onItemSelected: (BottomNavItem) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MovieMateSpacing.bottomBarHeight)
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(MovieMateSpacing.bottomBarHeight),
            containerColor = MovieMateColors.ScreenBackground,
            tonalElevation = MovieMateSpacing.noElevation
        ) {
            BottomNavItem.entries.forEach { item ->
                NavigationBarItem(
                    selected = selectedItem == item,
                    onClick = { onItemSelected(item) },
                    icon = {
                        Text(
                            text = item.iconText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MovieMateColors.AccentLight,
                        selectedTextColor = MovieMateColors.AccentLight,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = MovieMateColors.SecondaryText,
                        unselectedTextColor = MovieMateColors.SecondaryText
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(MovieMateSpacing.dividerThickness)
                .background(MovieMateColors.CardBorder)
        )
    }
}
