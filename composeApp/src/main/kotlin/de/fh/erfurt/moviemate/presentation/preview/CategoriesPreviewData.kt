package de.fh.erfurt.moviemate.presentation.preview

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoriesUiState
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.components.MovieCategoryTypes
import de.fh.erfurt.moviemate.presentation.movies.components.displayName

// Beispiel-Kategorien für Preview und Entwicklung ohne Backend.
// Die Filmzahlen werden aus denselben Mock-Filmen berechnet, die auch der Homescreen nutzt.
fun previewCategories(
    movies: List<Movie> = sampleMovies()
): List<CategoryUiModel> {
    val movieCountByCategory = movies
        .groupingBy { movie -> CategoryType.fromString(movie.categoryId) }
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
