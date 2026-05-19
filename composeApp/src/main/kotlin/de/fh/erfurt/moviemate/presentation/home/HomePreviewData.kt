package de.fh.erfurt.moviemate.presentation.home

import de.fh.erfurt.moviemate.presentation.mock.sampleMovies

// UI-Beispieldaten für den Homescreen. Sie nutzen bereits den echten UI-Mapper.
fun previewHomeMovies(): List<MovieUiModel> {
    return sampleMovies().map { it.toUiModel() }
}

// Vollständiger Beispiel-State für den Homescreen.
fun previewHomeUiState(): HomeUiState {
    return HomeUiState(
        movies = previewHomeMovies()
    )
}
