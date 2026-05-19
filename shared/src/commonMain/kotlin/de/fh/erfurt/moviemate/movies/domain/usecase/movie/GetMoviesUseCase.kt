package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class GetMoviesUseCase(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(): List<Movie> = movieRepository.getMovies()
}