package de.fh.erfurt.moviemate.presentation.preview

import de.fh.erfurt.moviemate.presentation.movies.home.HomeUiState
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel

// UI-Beispieldaten für den Homescreen. Sie nutzen bereits den echten UI-Mapper.
fun previewHomeMovies(): List<MovieUiModel> {
    return sampleMovies().map { it.toPreviewUiModel() }
}

// Vollständiger Beispiel-State für den Homescreen.
fun previewHomeUiState(): HomeUiState {
    return HomeUiState(
        movies = previewHomeMovies()
    )
}
