package de.fh.erfurt.moviemate.presentation

import de.fh.erfurt.moviemate.domain.model.CategoryType

/**
 * Interne Ziele der Mock-Navigation.
 *
 * Die BottomBar bleibt nur für Hauptbereiche zuständig. Detail ist dagegen ein
 * eigener Screen, der später von einer Filmkarte aus geöffnet wird.
 */
sealed interface MovieMateDestination {
    data class Home(val initialCategory: CategoryType? = null) : MovieMateDestination
    data object Categories : MovieMateDestination
    data class Detail(val movieId: String) : MovieMateDestination
    data object Add : MovieMateDestination
    data object Backup : MovieMateDestination
}

// Übersetzt BottomBar-Klicks in interne App-Ziele.
fun BottomNavItem.toDestination(): MovieMateDestination {
    return when (this) {
        BottomNavItem.Home -> MovieMateDestination.Home()
        BottomNavItem.Categories -> MovieMateDestination.Categories
        BottomNavItem.Add -> MovieMateDestination.Add
        BottomNavItem.Backup -> MovieMateDestination.Backup
    }
}

// Ermittelt, welcher BottomBar-Eintrag für ein Ziel aktiv angezeigt wird.
fun MovieMateDestination.selectedBottomNavItem(): BottomNavItem {
    return when (this) {
        is MovieMateDestination.Home -> BottomNavItem.Home
        MovieMateDestination.Categories -> BottomNavItem.Categories
        is MovieMateDestination.Detail -> BottomNavItem.Home
        MovieMateDestination.Add -> BottomNavItem.Add
        MovieMateDestination.Backup -> BottomNavItem.Backup
    }
}
