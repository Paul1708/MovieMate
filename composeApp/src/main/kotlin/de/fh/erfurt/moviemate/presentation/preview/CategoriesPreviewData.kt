package de.fh.erfurt.moviemate.presentation.preview

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.defaultIconKey
import de.fh.erfurt.moviemate.presentation.movies.components.MovieCategoryTypes
import de.fh.erfurt.moviemate.presentation.movies.components.displayName
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoriesUiState

// Beispiel-Kategorien für Preview und Entwicklung ohne Backend.
fun sampleCategories(): List<CategoryUiModel> {
    return MovieCategoryTypes.map { type ->
        CategoryUiModel(
            id = type.storageKey,
            name = type.displayName(),
            iconKey = type.defaultIconKey(),
        )
    }
}

fun previewCategories(
    movies: List<Movie> = sampleMovies(),
    categories: List<CategoryUiModel> = sampleCategories(),
): List<CategoryUiModel> {
    val movieCountByCategory = movies.groupingBy { movie -> movie.categoryId.orEmpty() }.eachCount()

    return categories.map { category ->
        val movieCount = movieCountByCategory[category.id] ?: 0
        category.copy(movieCountText = movieCount.toMovieCountText())
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
