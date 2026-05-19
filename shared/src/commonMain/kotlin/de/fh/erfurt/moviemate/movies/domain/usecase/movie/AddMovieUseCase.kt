package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class AddMovieUseCase(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(draft: MovieDraft): Movie {
        validateMovieDraft(draft)

        return movieRepository.addMovie(
            draft = draft.copy(
                title = draft.title.trim(),
                description = draft.description.trim(),
            ),
        )
    }
}