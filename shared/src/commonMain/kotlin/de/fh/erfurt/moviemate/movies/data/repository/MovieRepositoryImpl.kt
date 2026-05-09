package de.fh.erfurt.moviemate.movies.data.repository

import de.fh.erfurt.moviemate.movies.data.mapper.toDomain
import de.fh.erfurt.moviemate.movies.data.remote.MovieRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.remote.dto.CreateMovieRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateMovieRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateRatingRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateWatchStatusRequestDto
import de.fh.erfurt.moviemate.movies.domain.model.Category
import de.fh.erfurt.moviemate.movies.domain.model.CategoryType
import de.fh.erfurt.moviemate.movies.domain.model.Movie
import de.fh.erfurt.moviemate.movies.domain.model.SortCriterion
import de.fh.erfurt.moviemate.movies.domain.repository.MovieRepository

class MovieRepositoryImpl(
    private val remote: MovieRemoteDataSource
    // Später fügen wir hier die lokale Datenbank (SQLDelight) hinzu!
) : MovieRepository {

    override suspend fun getMovies(
        searchQuery: String?,
        categoryType: CategoryType?,
        isWatched: Boolean?,
        sortBy: SortCriterion
    ): List<Movie> {
        val remoteMovies = remote.fetchMovies()
        val mappedMovies = remoteMovies.map { it.toDomain(resolveCategory(it.category_id)) }
        val filteredMovies = mappedMovies.filter { movie ->
            matchesSearchQuery(movie, searchQuery) &&
                matchesCategoryType(movie, categoryType) &&
                matchesWatchStatus(movie, isWatched)
        }

        return filteredMovies.sortedWith(sortComparator(sortBy))
    }

    override suspend fun addMovie(movie: Movie) {
        remote.createMovie(movie.toCreateRequest())
    }

    override suspend fun updateMovie(movie: Movie) {
        remote.updateMovie(movie.id, movie.toUpdateRequest())
    }

    override suspend fun deleteMovie(id: String) {
        remote.deleteMovie(id)
    }

    override suspend fun updateWatchStatus(id: String, isWatched: Boolean) {
        remote.updateWatchStatus(id, UpdateWatchStatusRequestDto(watched = isWatched))
    }

    override suspend fun updateRating(id: String, rating: Int) {
        remote.updateRating(id, UpdateRatingRequestDto(rating_score = rating))
    }

    private fun resolveCategory(categoryId: String): Category {
        // Normalisiere die ID, damit verschiedene Schreibweisen (z. B. "Sci-Fi", "science_fiction")
        // auf denselben CategoryType gemappt werden können.
        val normalized = categoryId.trim().lowercase()
            .replace(" ", "_")
            .replace('-', '_')

        val type = when (normalized) {
            "drama" -> CategoryType.DRAMA
            "action" -> CategoryType.ACTION
            "fantasy" -> CategoryType.FANTASY
            "romance", "romantic" -> CategoryType.ROMANCE
            "comedy", "comedies" -> CategoryType.COMEDY
            "horror" -> CategoryType.HORROR
            "favorite", "favourite", "fav", "favorites" -> CategoryType.FAVORITE
            "science_fiction", "sciencefiction", "scifi", "sci_fi", "sci-fi" -> CategoryType.SCIENCE_FICTION
            "animation", "animated", "cartoon" -> CategoryType.ANIMATION
            else -> CategoryType.OTHER
        }

        return Category(id = categoryId, type = type)
    }

    private fun matchesSearchQuery(movie: Movie, searchQuery: String?): Boolean {
        val normalizedQuery = searchQuery?.trim().orEmpty()
        if (normalizedQuery.isBlank()) return true

        val query = normalizedQuery.lowercase()
        return movie.title.lowercase().contains(query)
    }

    private fun matchesCategoryType(movie: Movie, categoryType: CategoryType?): Boolean {
        if (categoryType == null) return true

        return when (categoryType) {
            CategoryType.FAVORITE -> movie.isWatched || movie.category.type == CategoryType.FAVORITE
            else -> movie.category.type == categoryType
        }
    }

    private fun matchesWatchStatus(movie: Movie, isWatched: Boolean?): Boolean {
        return isWatched == null || movie.isWatched == isWatched
    }

    private fun sortComparator(sortBy: SortCriterion) = when (sortBy) {
        SortCriterion.TITLE_ASC -> compareBy<Movie> { it.title.lowercase() }
        SortCriterion.TITLE_DESC -> compareByDescending<Movie> { it.title.lowercase() }
        SortCriterion.PUBLISHING_YEAR_DESC -> compareByDescending<Movie> { it.publishingYear }
        SortCriterion.RATING_DESC -> compareByDescending<Movie> { it.rating }
        SortCriterion.RECENTLY_ADDED -> compareByDescending<Movie> { it.creationTimeEpochMillis }
    }

    private fun Movie.toCreateRequest(): CreateMovieRequestDto {
        return CreateMovieRequestDto(
            id = id,
            title = title,
            poster_url = posterUri,
            release_year = publishingYear,
            runtime_mins = runtime,
            rating_score = rating,
            watched = isWatched,
            category_id = category.id,
            desc = description,
            created_at = creationTimeEpochMillis,
            updated_at = updateTimeEpochMillis
        )
    }

    private fun Movie.toUpdateRequest(): UpdateMovieRequestDto {
        return UpdateMovieRequestDto(
            id = id,
            title = title,
            poster_url = posterUri,
            release_year = publishingYear,
            runtime_mins = runtime,
            rating_score = rating,
            watched = isWatched,
            category_id = category.id,
            desc = description,
            created_at = creationTimeEpochMillis,
            updated_at = updateTimeEpochMillis
        )
    }
}