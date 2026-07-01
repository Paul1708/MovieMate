package de.fh.erfurt.moviemate.presentation.preview

import de.fh.erfurt.moviemate.presentation.movies.home.HomeUiState
import de.fh.erfurt.moviemate.presentation.movies.mapper.toHomeUiModel

fun previewHomeUiState(): HomeUiState {
    val categories = sampleCategories()
    return HomeUiState(
        movies = sampleMovies().map { movie -> movie.toHomeUiModel(categories) },
        availableCategories = categories,
    )
}
