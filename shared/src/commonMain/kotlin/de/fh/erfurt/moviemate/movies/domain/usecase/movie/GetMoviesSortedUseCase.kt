package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy

class GetMoviesSortedUseCase(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(sortBy: SortBy, ascending: Boolean = true): List<Movie> {
        return movieRepository.getMoviesSorted(sortBy = sortBy, ascending = ascending)
    }
}