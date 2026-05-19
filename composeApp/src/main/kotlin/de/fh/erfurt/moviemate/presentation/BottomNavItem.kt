package de.fh.erfurt.moviemate.presentation

// Einträge der unteren Navigation.
// Das Enum kann später direkt für die echte Navigation verwendet werden.
enum class BottomNavItem(
    val label: String,
    val iconText: String
) {
    Home("Home", "⌂"),
    Categories("Categories", "▦"),
    Add("Add", "+"),
    Backup("Backup", "≡")
}
