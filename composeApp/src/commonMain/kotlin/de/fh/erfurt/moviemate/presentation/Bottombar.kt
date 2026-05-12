package de.fh.erfurt.moviemate.presentation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

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
    NavigationBar(
        containerColor = MovieMateColors.ScreenBackground,
        tonalElevation = 0.dp
    ) {
        BottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item,
                onClick = { onItemSelected(item) },
                icon = { Text(item.iconText) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MovieMateColors.PrimaryText,
                    selectedTextColor = MovieMateColors.PrimaryText,
                    indicatorColor = MovieMateColors.SoftControl,
                    unselectedIconColor = MovieMateColors.SecondaryText,
                    unselectedTextColor = MovieMateColors.SecondaryText
                )
            )
        }
    }
}
