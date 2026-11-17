package de.fh.erfurt.moviemate.platform

import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

/**
 * Objective-C/Swift friendly facade around the shared domain layer.
 *
 * Optional numeric values deliberately cross the Apple boundary as strings. This avoids
 * Kotlin number wrapper types in Swift while keeping validation in the shared domain layer.
 */
class IosMovieMateApi internal constructor(
    private val appContainer: AppContainer,
) {
    @Throws(Exception::class)
    suspend fun loadLibrary(): IosLibrarySnapshot {
        val categories = appContainer.getCategoriesUseCase()
        val movies = appContainer.getMoviesUseCase()
        return IosLibrarySnapshot(
            movies = movies.map(Movie::toIosRecord),
            categories = categories.map { category ->
                IosCategoryRecord(
                    id = category.id,
                    name = category.name,
                    iconKey = category.iconKey.orEmpty(),
                )
            },
        )
    }

    @Throws(Exception::class)
    suspend fun addMovie(form: IosMovieForm): IosMovieRecord {
        val savedMovie = appContainer.addMovieUseCase(
            MovieDraft(
                title = form.title,
                description = form.description,
                releaseYear = form.releaseYear.toOptionalInt("Release year"),
                categoryId = form.categoryId.normalizedOrNull(),
                posterUrl = form.posterUrl.normalizedOrNull(),
                runtimeMinutes = form.runtimeMinutes.toOptionalInt("Runtime"),
                isWatched = form.isWatched,
                comment = form.comment.normalizedOrNull(),
                userRating = form.userRating.toOptionalInt("User rating"),
                imdbRating = form.imdbRating.toOptionalDouble("IMDb rating"),
                externalIds = null,
                watchProviders = form.watchProviders.normalizedProviders(),
                updatedAtEpochMillis = currentTimeMillis(),
            ),
        )
        return savedMovie.toIosRecord()
    }

    @Throws(Exception::class)
    suspend fun updateMovie(movieId: String, form: IosMovieForm): IosMovieRecord {
        val currentMovie = appContainer.getMovieUseCase(movieId)
            ?: throw NoSuchElementException("Movie not found: $movieId")
        val savedMovie = appContainer.updateMovieUseCase(
            currentMovie.copy(
                title = form.title,
                description = form.description,
                releaseYear = form.releaseYear.toOptionalInt("Release year"),
                categoryId = form.categoryId.normalizedOrNull(),
                posterUrl = form.posterUrl.normalizedOrNull(),
                runtimeMinutes = form.runtimeMinutes.toOptionalInt("Runtime"),
                isWatched = form.isWatched,
                comment = form.comment.normalizedOrNull(),
                userRating = form.userRating.toOptionalInt("User rating"),
                imdbRating = form.imdbRating.toOptionalDouble("IMDb rating"),
                watchProviders = form.watchProviders.normalizedProviders(),
                updatedAtEpochMillis = currentTimeMillis(),
            ),
        )
        return savedMovie.toIosRecord()
    }

    @Throws(Exception::class)
    suspend fun setMovieWatched(movieId: String, isWatched: Boolean): IosMovieRecord {
        val currentMovie = appContainer.getMovieUseCase(movieId)
            ?: throw NoSuchElementException("Movie not found: $movieId")
        return appContainer.updateMovieUseCase(
            currentMovie.copy(
                isWatched = isWatched,
                updatedAtEpochMillis = currentTimeMillis(),
            ),
        ).toIosRecord()
    }

    @Throws(Exception::class)
    suspend fun deleteMovie(movieId: String) {
        appContainer.deleteMovieUseCase(movieId)
    }

    @Throws(Exception::class)
    suspend fun addCategory(name: String, iconKey: String): IosCategoryRecord {
        val category = appContainer.addCategoryUseCase(
            CategoryDraft(
                name = name,
                iconKey = iconKey.normalizedOrNull(),
                updatedAtEpochMillis = currentTimeMillis(),
            ),
        )
        return IosCategoryRecord(
            id = category.id,
            name = category.name,
            iconKey = category.iconKey.orEmpty(),
        )
    }

    @Throws(Exception::class)
    suspend fun deleteCategory(categoryId: String) {
        appContainer.deleteCategoryUseCase(categoryId)
    }

    @Throws(Exception::class)
    suspend fun searchMovieMetadata(title: String, releaseYear: String): IosMovieSearchResults {
        val result = appContainer.fetchMovieDataUseCase.search(title, releaseYear)
        return IosMovieSearchResults(
            matches = result.matches.map {
                IosMovieSearchMatch(it.imdbId, it.title, it.year, it.posterUrl.orEmpty())
            },
            totalResults = result.totalResults,
        )
    }

    @Throws(Exception::class)
    suspend fun fetchMovieData(imdbId: String): IosFetchedMovieRecord? {
        val movie = appContainer.fetchMovieDataUseCase.byImdbId(imdbId) ?: return null
        return IosFetchedMovieRecord(
            title = movie.title,
            description = movie.description,
            releaseYear = movie.releaseYear?.toString().orEmpty(),
            posterUrl = movie.posterUrl.orEmpty(),
            runtimeMinutes = movie.runtimeMinutes?.toString().orEmpty(),
            imdbRating = movie.imdbRating?.toString().orEmpty(),
            genres = movie.genres,
        )
    }

    @Throws(Exception::class)
    suspend fun exportBackup(): String = appContainer.exportBackupUseCase()

    @Throws(Exception::class)
    suspend fun importBackup(json: String) {
        appContainer.importBackupUseCase(json)
    }

    @Throws(Exception::class)
    suspend fun synchronizeGoogleDrive(accessToken: String, accountId: String): IosSyncResult {
        val result = appContainer.syncWithGoogleDriveUseCase(
            accessToken = accessToken,
            accountId = accountId,
        )
        return IosSyncResult(
            movieCount = result.movieCount,
            categoryCount = result.categoryCount,
            remoteSnapshotCount = result.remoteSnapshotCount,
            localDataChanged = result.localDataChanged,
        )
    }
}

data class IosLibrarySnapshot(
    val movies: List<IosMovieRecord>,
    val categories: List<IosCategoryRecord>,
)

data class IosMovieRecord(
    val id: String,
    val title: String,
    val description: String,
    val releaseYear: String,
    val categoryId: String,
    val posterUrl: String,
    val runtimeMinutes: String,
    val isWatched: Boolean,
    val comment: String,
    val userRating: String,
    val imdbRating: String,
    val watchProviders: List<String>,
    val updatedAtEpochMillis: Long,
)

data class IosCategoryRecord(
    val id: String,
    val name: String,
    val iconKey: String,
)

data class IosMovieForm(
    val title: String,
    val description: String,
    val categoryId: String,
    val posterUrl: String,
    val releaseYear: String,
    val runtimeMinutes: String,
    val userRating: String,
    val imdbRating: String,
    val isWatched: Boolean,
    val comment: String,
    val watchProviders: List<String>,
)

data class IosMovieSearchResults(
    val matches: List<IosMovieSearchMatch>,
    val totalResults: Int,
)

data class IosMovieSearchMatch(
    val imdbId: String,
    val title: String,
    val year: String,
    val posterUrl: String,
)

data class IosFetchedMovieRecord(
    val title: String,
    val description: String,
    val releaseYear: String,
    val posterUrl: String,
    val runtimeMinutes: String,
    val imdbRating: String,
    val genres: List<String>,
)

data class IosSyncResult(
    val movieCount: Int,
    val categoryCount: Int,
    val remoteSnapshotCount: Int,
    val localDataChanged: Boolean,
)

private fun Movie.toIosRecord(): IosMovieRecord = IosMovieRecord(
    id = id,
    title = title,
    description = description,
    releaseYear = releaseYear?.toString().orEmpty(),
    categoryId = categoryId.orEmpty(),
    posterUrl = posterUrl.orEmpty(),
    runtimeMinutes = runtimeMinutes?.toString().orEmpty(),
    isWatched = isWatched,
    comment = comment.orEmpty(),
    userRating = userRating?.toString().orEmpty(),
    imdbRating = imdbRating?.toString().orEmpty(),
    watchProviders = watchProviders.orEmpty(),
    updatedAtEpochMillis = updatedAtEpochMillis,
)

private fun String.normalizedOrNull(): String? = trim().takeIf(String::isNotEmpty)

private fun List<String>.normalizedProviders(): List<String>? {
    return map(String::trim).filter(String::isNotEmpty).distinct().takeIf { it.isNotEmpty() }
}

private fun String.toOptionalInt(fieldName: String): Int? {
    val normalized = trim()
    if (normalized.isEmpty()) return null
    return normalized.toIntOrNull()
        ?: throw IllegalArgumentException("$fieldName must be a whole number")
}

private fun String.toOptionalDouble(fieldName: String): Double? {
    val normalized = trim().replace(',', '.')
    if (normalized.isEmpty()) return null
    return normalized.toDoubleOrNull()
        ?: throw IllegalArgumentException("$fieldName must be a number")
}

private fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970() * 1_000.0).toLong()
