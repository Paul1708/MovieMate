package de.fh.erfurt.moviemate.presentation.preview

import de.fh.erfurt.moviemate.presentation.movies.home.HomeUiState
import de.fh.erfurt.moviemate.presentation.movies.mapper.toHomeUiModel
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel

// UI-Beispieldaten für den Homescreen. Sie nutzen bereits den gemeinsamen UI-Mapper.
fun previewHomeMovies(): List<MovieUiModel> {
    val categories = sampleCategories()
    return sampleMovies().map { it.toHomeUiModel(categories) }
}

// Vollständiger Beispiel-State für den Homescreen.
fun previewHomeUiState(): HomeUiState {
    val categories = sampleCategories()
    return HomeUiState(
        movies = sampleMovies().map { it.toHomeUiModel(categories) },
        availableCategories = categories,
    )
}
