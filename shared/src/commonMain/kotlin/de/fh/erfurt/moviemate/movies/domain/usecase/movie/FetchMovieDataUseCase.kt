package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieMetadataRepository

class FetchMovieDataUseCase(
    private val movieMetadataRepository: MovieMetadataRepository,
) {
    suspend operator fun invoke(title: String): FetchedMovieData? {
        val trimmedTitle = title.trim()

        if (trimmedTitle.isBlank()) {
            return null
        }

        return movieMetadataRepository.fetchMovieDataByTitle(trimmedTitle)
    }
}
