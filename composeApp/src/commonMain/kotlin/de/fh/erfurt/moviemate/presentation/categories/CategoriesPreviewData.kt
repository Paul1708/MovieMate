package de.fh.erfurt.moviemate.presentation.categories

import de.fh.erfurt.moviemate.domain.model.Movie
import de.fh.erfurt.moviemate.presentation.MovieCategoryTypes
import de.fh.erfurt.moviemate.presentation.displayName
import de.fh.erfurt.moviemate.presentation.home.previewDomainMovies

// Beispiel-Kategorien für Preview und Entwicklung ohne Backend.
// Die Filmzahlen werden aus denselben Mock-Filmen berechnet, die auch der Homescreen nutzt.
fun previewCategories(
    movies: List<Movie> = previewDomainMovies()
): List<CategoryUiModel> {
    val movieCountByCategory = movies
        .groupingBy { movie -> movie.category.type }
        .eachCount()

    return MovieCategoryTypes.map { type ->
        val movieCount = movieCountByCategory[type] ?: 0

        CategoryUiModel(
            type = type,
            label = type.displayName(),
            movieCountText = movieCount.toMovieCountText()
        )
    }
}

// Vollständiger Beispiel-State für den Kategorie-Screen.
fun previewCategoriesUiState(): CategoriesUiState {
    return CategoriesUiState(
        categories = previewCategories()
    )
}

private fun Int.toMovieCountText(): String {
    return if (this == 1) {
        "1 Movie"
    } else {
        "$this Movies"
    }
}
