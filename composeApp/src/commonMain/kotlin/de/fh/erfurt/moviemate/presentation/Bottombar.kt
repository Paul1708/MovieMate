package de.fh.erfurt.moviemate.presentation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

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
    NavigationBar {
        BottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item,
                onClick = { onItemSelected(item) },
                icon = { Text(item.iconText) },
                label = { Text(item.label) }
            )
        }
    }
}
