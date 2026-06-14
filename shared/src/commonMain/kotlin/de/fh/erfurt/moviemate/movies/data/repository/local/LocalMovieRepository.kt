package de.fh.erfurt.moviemate.movies.data.repository.local

import de.fh.erfurt.moviemate.movies.data.local.datasource.CategoryLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.datasource.MovieLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.LocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.MovieLocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.model.LocalCategoryEntity
import de.fh.erfurt.moviemate.movies.data.local.model.LocalMovieEntity
import de.fh.erfurt.moviemate.movies.data.mapper.local.toDomainModel
import de.fh.erfurt.moviemate.movies.data.mapper.local.toLocalEntity
import de.fh.erfurt.moviemate.movies.data.remote.dto.BackupDataDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import kotlinx.serialization.json.Json

class LocalMovieRepository(
    private val localDataSource: MovieLocalDataSource,
    private val categoryDataSource: CategoryLocalDataSource,
    private val idPolicy: LocalIdPolicy = MovieLocalIdPolicy,
) : MovieRepository {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    override suspend fun getMovies(): List<Movie> {
        return localDataSource.getMovies().map { entity -> entity.toDomainModel() }
    }

    override suspend fun getMovie(movieId: String): Movie? {
        return localDataSource.getMovie(movieId)?.toDomainModel()
    }

    override suspend fun addMovie(draft: MovieDraft): Movie {
        val existingIds = localDataSource.getMovies().map { it.id }
        val storedMovie = localDataSource.insertMovie(
            draft.toLocalEntity(idPolicy.nextId(existingIds)),
        )
        return storedMovie.toDomainModel()
    }

    override suspend fun updateMovie(movie: Movie): Movie {
        return localDataSource.updateMovie(movie.toLocalEntity()).toDomainModel()
    }

    override suspend fun deleteMovie(movieId: String) {
        localDataSource.deleteMovie(movieId)
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return localDataSource.searchMovies(query).map { entity -> entity.toDomainModel() }
    }

    override suspend fun getMoviesByCategory(categoryId: String): List<Movie> {
        return localDataSource.getMoviesByCategory(categoryId).map { entity -> entity.toDomainModel() }
    }

    override suspend fun getMoviesSorted(sortBy: SortBy, ascending: Boolean): List<Movie> {
        val comparator = when (sortBy) {
            SortBy.TITLE -> compareBy<Movie> { it.title.lowercase() }
            SortBy.RELEASE_YEAR -> compareBy<Movie> { it.releaseYear ?: Int.MIN_VALUE }
            SortBy.UPDATED_AT -> compareBy<Movie> { it.updatedAtEpochMillis }
            SortBy.RATING -> compareBy<Movie> { it.userRating ?: Int.MIN_VALUE }
            SortBy.RUNTIME -> compareBy<Movie> { it.runtimeMinutes ?: Int.MIN_VALUE }
        }

        val sortedMovies = getMovies().sortedWith(comparator)
        return if (ascending) sortedMovies else sortedMovies.reversed()
    }

    suspend fun updateMovieWatchedStatus(movieId: String, isWatched: Boolean, updatedAtEpochMillis: Long) {
        localDataSource.updateMovieWatchedStatus(movieId, isWatched, updatedAtEpochMillis)
    }

    suspend fun updateMovieUserRating(movieId: String, userRating: Int?, updatedAtEpochMillis: Long) {
        localDataSource.updateMovieUserRating(movieId, userRating, updatedAtEpochMillis)
    }

    override suspend fun exportBackup(): String {
        val moviesDto = getMovies().map { movie ->
            MovieDto(
                id = movie.id,
                title = movie.title,
                description = movie.description,
                releaseYear = movie.releaseYear,
                categoryId = movie.categoryId,
                posterUrl = movie.posterUrl,
                runtimeMinutes = movie.runtimeMinutes,
                isWatched = movie.isWatched,
                comment = movie.comment,
                userRating = movie.userRating,
                imdbRating = movie.imdbRating,
                externalIds = movie.externalIds,
                watchProviders = movie.watchProviders,
                updatedAtEpochMillis = movie.updatedAtEpochMillis,
                version = movie.version
            )
        }

        val categoriesDto = categoryDataSource.getCategories().map { category ->
            CategoryDto(
                id = category.id,
                name = category.name,
                updatedAtEpochMillis = category.updatedAtEpochMillis,
                version = category.version
            )
        }

        val backup = BackupDataDto(movies = moviesDto, categories = categoriesDto)
        return jsonSerializer.encodeToString(backup)
    }

    override suspend fun importBackup(jsonString: String) {
        val backup = jsonSerializer.decodeFromString<BackupDataDto>(jsonString)

        backup.categories.forEach { dto ->
            val entity = LocalCategoryEntity(
                id = dto.id,
                name = dto.name,
                updatedAtEpochMillis = dto.updatedAtEpochMillis,
                version = dto.version
            )
            if (categoryDataSource.getCategory(entity.id) != null) {
                categoryDataSource.updateCategory(entity)
            } else {
                categoryDataSource.insertCategory(entity)
            }
        }

        backup.movies.forEach { dto ->
            val entity = LocalMovieEntity(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                releaseYear = dto.releaseYear?.toLong(),
                categoryId = dto.categoryId,
                posterUrl = dto.posterUrl,
                runtimeMinutes = dto.runtimeMinutes?.toLong(),
                isWatched = if (dto.isWatched) 1L else 0L,
                comment = dto.comment,
                userRating = dto.userRating?.toLong(),
                imdbRating = dto.imdbRating,
                externalIds = dto.externalIds?.let { jsonSerializer.encodeToString(it) },
                watchProviders = dto.watchProviders?.let { jsonSerializer.encodeToString(it) },
                updatedAtEpochMillis = dto.updatedAtEpochMillis,
                version = dto.version
            )
            if (localDataSource.getMovie(entity.id) != null) {
                localDataSource.updateMovie(entity)
            } else {
                localDataSource.insertMovie(entity)
            }
        }
    }



}