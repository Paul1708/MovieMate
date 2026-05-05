package de.fh.erfurt.moviemate.data.mapper

import de.fh.erfurt.moviemate.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.domain.model.Category
import de.fh.erfurt.moviemate.domain.model.Movie


fun MovieDto.toDomain(category: Category): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        posterUri = this.poster_url,
        publishingYear = this.release_year,
        runtime = this.runtime_mins,
        rating = this.rating_score,
        isWatched = this.watched,
        category = category,
        description = this.desc,
        creationTimeEpochMillis = this.created_at,
        updateTimeEpochMillis = this.updated_at
    )
}