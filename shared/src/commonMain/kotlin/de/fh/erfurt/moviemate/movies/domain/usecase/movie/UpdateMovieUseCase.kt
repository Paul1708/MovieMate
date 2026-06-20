package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class UpdateMovieUseCase(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(movie: Movie): Movie {
        validateMovieFields(movie)

        return movieRepository.updateMovie(
            movie = movie.copy(
                title = movie.title.trim(),
                description = movie.description.trim(),
            ),
        )
    }
}