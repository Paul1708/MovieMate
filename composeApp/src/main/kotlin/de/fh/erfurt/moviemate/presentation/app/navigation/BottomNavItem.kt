package de.fh.erfurt.moviemate.presentation.app.navigation

import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons

// Einträge der unteren Navigation.
// Das Enum kann später direkt für die echte Navigation verwendet werden.
enum class BottomNavItem(
    val label: String,
    val iconText: String
) {
    Home("Home", MovieMateIcons.home),
    Categories("Categories", MovieMateIcons.categories),
    Add("Add", MovieMateIcons.add),
    Backup("Backup", MovieMateIcons.backup)
}
