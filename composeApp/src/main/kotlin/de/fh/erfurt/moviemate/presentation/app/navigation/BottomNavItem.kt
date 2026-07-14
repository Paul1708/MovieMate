package de.fh.erfurt.moviemate.presentation.app.navigation

import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons

/** Semantische Einträge der Bottom-Navigation; Routen werden separat zugeordnet. */
enum class BottomNavItem(
    val label: String,
    val icon: Int,
) {
    Home("Home", MovieMateIcons.home),
    Categories("Categories", MovieMateIcons.category),
    Add("Add", MovieMateIcons.addCircle),
    Backup("Backup", MovieMateIcons.backup),
}
