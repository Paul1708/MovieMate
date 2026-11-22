package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft

internal fun validateMovieDraft(draft: MovieDraft) {
    require(draft.title.isNotBlank()) { "Title cannot be empty" }
    draft.runtimeMinutes?.let { rt -> require(rt > 0) { "Runtime must be positive" } }
    validateSeasonCount(draft.mediaType, draft.totalSeasons)
    draft.userRating?.let { ur -> require(ur in 1..10) { "User rating must be between 1 and 10" } }
    draft.imdbRating?.let { ir -> require(ir in 0.0..10.0) { "IMDb rating must be between 0.0 and 10.0" } }
}
internal fun validateSeasonCount(mediaType: MediaType, totalSeasons: Int?) {
    require(totalSeasons == null || (mediaType == MediaType.SERIES && totalSeasons > 0)) {
        "Seasons must be a positive whole number for a series."
    }
}
