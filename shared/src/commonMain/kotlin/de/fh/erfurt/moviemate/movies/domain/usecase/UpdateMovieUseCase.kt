package de.fh.erfurt.moviemate.movies.domain.usecase

import de.fh.erfurt.moviemate.movies.domain.model.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.MovieRepository

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

        repository.updateMovie(movie)
    }
}