package de.fh.erfurt.moviemate.presentation.home

// Beispiel-Filme für Preview und Entwicklung ohne Backend.
fun previewHomeMovies(): List<MovieUiModel> {
    return listOf(
        MovieUiModel(
            id = "movie-1",
            title = "Movie Title",
            ratingText = "8,5",
            categoryText = "Action",
            yearText = "2019",
            runtimeText = "130 Minutes",
            posterUri = null,
            isWatched = true
        ),
        MovieUiModel(
            id = "movie-2",
            title = "Movie Title",
            ratingText = "5,5",
            categoryText = "Drama",
            yearText = "2025",
            runtimeText = "60 Minutes",
            posterUri = null,
            isWatched = false
        )
    )
}

// Vollstaendiger Beispiel-State fuer den Homescreen.
fun previewHomeUiState(): HomeUiState {
    return HomeUiState(
        movies = previewHomeMovies()
    )
}
