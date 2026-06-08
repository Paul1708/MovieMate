package de.fh.erfurt.moviemate.presentation.movies.home

// Filter-Tabs, die oben im Homescreen angezeigt werden.
enum class HomeTab(
    val label: String
) {
    All("All"),
    Watched("Watched"),
    WatchLater("Watchlist")
}
