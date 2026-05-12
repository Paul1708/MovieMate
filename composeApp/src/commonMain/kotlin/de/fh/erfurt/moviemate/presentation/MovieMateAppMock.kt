package de.fh.erfurt.moviemate.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.categories.CategoriesRouteMock
import de.fh.erfurt.moviemate.presentation.home.HomeRouteMock

/**
 * Gemeinsamer App-Rahmen für die aktuelle Mock-UI.
 *
 * Diese Composable hält die aktive BottomBar-Auswahl und zeigt den passenden
 * Screen an. Später kann diese einfache Logik durch echte Navigation ersetzt
 * werden.
 */
@Composable
fun MovieMateAppMock() {
    var selectedItem by remember { mutableStateOf(BottomNavItem.Home) }

    Scaffold(
        containerColor = MovieMateColors.ScreenBackground,
        bottomBar = {
            BottomBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { paddingValues ->
        val contentModifier = Modifier.padding(paddingValues)

        when (selectedItem) {
            BottomNavItem.Home -> HomeRouteMock(modifier = contentModifier)
            BottomNavItem.Categories -> CategoriesRouteMock(modifier = contentModifier)
            BottomNavItem.Add -> HomeRouteMock(modifier = contentModifier)
            BottomNavItem.Backup -> HomeRouteMock(modifier = contentModifier)
        }
    }
}
