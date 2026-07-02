package de.fh.erfurt.moviemate.presentation.app.navigation

// Einträge der unteren Navigation.
// Das Enum kann später direkt für die echte Navigation verwendet werden.
enum class BottomNavItem(
    val label: String,
    val icon: BottomNavIconType
) {
    Home("Home", BottomNavIconType.Home),
    Categories("Categories", BottomNavIconType.Categories),
    Add("Add", BottomNavIconType.Add),
    Backup("Backup", BottomNavIconType.Backup)
}
