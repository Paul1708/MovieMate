package de.fh.erfurt.moviemate.presentation.home

// Beispiel-Filme für Preview und Entwicklung ohne Backend.
fun previewHomeMovies(): List<MovieUiModel> {
    return listOf(
        MovieUiModel(
            id = "movie-1",
            title = "Star Wars",
            ratingText = "10,0",
            categoryText = "Action",
            yearText = "2019",
            runtimeText = "130 Minutes",
            posterUri = null,
            isWatched = true
        ),
        MovieUiModel(
            id = "movie-2",
            title = "Harry Potter",
            ratingText = "5,5",
            categoryText = "Drama",
            yearText = "2025",
            runtimeText = "60 Minutes",
            posterUri = null,
            isWatched = false
        ),
        MovieUiModel(
            id = "movie-3",
            title = "The Matrix",
            ratingText = "8,5",
            categoryText = "Sci-Fi",
            yearText = "1999",
            runtimeText = "136 Minutes",
            posterUri = null,
            isWatched = true
        ),
        MovieUiModel(
            id = "movie-4",
            title = "Inception",
            ratingText = "9,0",
            categoryText = "Thriller",
            yearText = "2010",
            runtimeText = "148 Minutes",
            posterUri = null,
            isWatched = false
    ),
        MovieUiModel(
            id = "movie-5",
            title = "The Godfather",
            ratingText = "9,2",
            categoryText = "Crime",
            yearText = "1972",
            runtimeText = "175 Minutes",
            posterUri = null,
            isWatched = true
        )
    )
}

// Vollständiger Beispiel-State für den Homescreen.
fun previewHomeUiState(): HomeUiState {
    return HomeUiState(
        movies = previewHomeMovies()
    )
}
