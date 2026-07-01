package de.fh.erfurt.moviemate.presentation.movies.detail

import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel

/**
 * Local form state for the detail edit mode.
 */
data class MovieDetailEditState(
    val title: String = "",
    val description: String = "",
    val posterUrl: String? = null,
    val releaseYear: String = "",
    val categoryId: String = "",
    val runtimeMinutes: String = "",
    val isWatched: Boolean = false,
    val comment: String = "",
    val userRating: String = "",
    val imdbRating: String = "",
    val watchProviders: List<String> = emptyList(),
    val watchProviderInput: String = ""
) {
    companion object {
        fun from(movie: MovieDetailUiModel): MovieDetailEditState {
            return MovieDetailEditState(
                title = movie.title,
                description = movie.description,
                posterUrl = movie.posterUrl,
                releaseYear = movie.releaseYear?.toString().orEmpty(),
                categoryId = movie.categoryId.ifBlank { movie.categoryName },
                runtimeMinutes = movie.runtimeMinutes?.toString().orEmpty(),
                isWatched = movie.isWatched,
                comment = movie.comment.orEmpty(),
                userRating = movie.userRating?.toString().orEmpty(),
                imdbRating = movie.imdbRating?.toString().orEmpty(),
                watchProviders = movie.watchProviders
            )
        }
    }

    fun applyTo(
        movie: MovieDetailUiModel,
        categories: List<CategoryUiModel> = emptyList(),
    ): MovieDetailUiModel {
        val selectedCategoryId = categoryId.trim()
        val selectedCategory = categories.firstOrNull { it.id == selectedCategoryId }

        return movie.copy(
            title = title.trim(),
            description = description.trim(),
            posterUrl = posterUrl,
            releaseYear = releaseYear.trim().toIntOrNull(),
            categoryId = selectedCategoryId,
            categoryName = selectedCategory?.name ?: selectedCategoryId,
            categoryIconKey = selectedCategory?.iconKey ?: movie.categoryIconKey,
            runtimeMinutes = runtimeMinutes.trim().toIntOrNull(),
            isWatched = isWatched,
            comment = comment.trim().ifBlank { null },
            userRating = userRating.trim().toIntOrNull()?.coerceIn(0, 10),
            imdbRating = imdbRating.trim().toDoubleOrNull()?.coerceIn(0.0, 10.0),
            watchProviders = normalizedWatchProviders()
        )
    }

    fun withAddedWatchProvider(): MovieDetailEditState {
        val additions = watchProviderInput.toWatchProviderList()
        if (additions.isEmpty()) return this

        return copy(
            watchProviders = (watchProviders + additions).distinctBy { it.lowercase() },
            watchProviderInput = ""
        )
    }

    fun withoutWatchProvider(provider: String): MovieDetailEditState {
        return copy(
            watchProviders = watchProviders.filterNot { it == provider }
        )
    }

    private fun normalizedWatchProviders(): List<String> {
        return (watchProviders + watchProviderInput.toWatchProviderList())
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
    }
}

private fun String.toWatchProviderList(): List<String> {
    return split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
}
