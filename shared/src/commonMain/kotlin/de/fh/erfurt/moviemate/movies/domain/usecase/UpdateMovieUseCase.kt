package de.fh.erfurt.moviemate.movies.domain.usecase

import de.fh.erfurt.moviemate.movies.domain.model.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.MovieRepository
import kotlin.time.Clock

class UpdateMovieUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) {
        require(movie.id.isNotBlank()) { "The movie id cannot be blank." }
        require(movie.title.isNotBlank()) { "The movie title cannot be left blank." }
        require(movie.publishingYear > 1888) { "The publication year is invalid." }
        require(movie.runtime > 0) { "The runtime must be greater than 0 minutes." }
        require(movie.rating in 1..10 ) { "The rating must be between 1 and 10." }
        require(movie.category.id.isNotBlank()) { "The category id cannot be blank." }

        val currentTime = Clock.System.now().toEpochMilliseconds()

        val newMovie = Movie(
            id = movie.id,
            title = movie.title,
            posterUri = movie.posterUri,
            publishingYear = movie.publishingYear,
            runtime = movie.runtime,
            rating = movie.rating,
            isWatched = movie.isWatched,
            category = movie.category,
            description = movie.description,
            creationTimeEpochMillis = movie.creationTimeEpochMillis,
            updateTimeEpochMillis = currentTime
        )

        repository.updateMovie(newMovie)
    }
}