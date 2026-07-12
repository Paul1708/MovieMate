package de.fh.erfurt.moviemate.presentation.app.navigation

// Einträge der unteren Navigation.
// Semantische Einträge der Bottom-Navigation; konkrete Routen werden separat zugeordnet.
enum class BottomNavItem(
    val label: String,
    val icon: BottomNavIconType
) {
    Home("Home", BottomNavIconType.Home),
    Categories("Categories", BottomNavIconType.Categories),
    Add("Add", BottomNavIconType.Add),
    Backup("Backup", BottomNavIconType.Backup)
}
