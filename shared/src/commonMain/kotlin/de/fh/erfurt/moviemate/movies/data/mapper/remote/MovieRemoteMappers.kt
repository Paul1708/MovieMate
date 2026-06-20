package de.fh.erfurt.moviemate.movies.data.mapper.remote

import de.fh.erfurt.moviemate.movies.data.remote.dto.CreateMovieRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateMovieRequestDto
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft

fun MovieDto.toDomainModel(): Movie = Movie(
    id = id,
    title = title,
    description = description,
    releaseYear = releaseYear,
    categoryId = categoryId,
    posterUrl = posterUrl,
    runtimeMinutes = runtimeMinutes,
    isWatched = isWatched,
    comment = comment,
    userRating = userRating,
    imdbRating = imdbRating,
    externalIds = externalIds,
    watchProviders = watchProviders,
    updatedAtEpochMillis = updatedAtEpochMillis,
    version = version,
)

fun MovieDraft.toCreateRequestDto(): CreateMovieRequestDto = CreateMovieRequestDto(
    title = title,
    description = description,
    releaseYear = releaseYear,
    categoryId = categoryId,
    posterUrl = posterUrl,
    runtimeMinutes = runtimeMinutes,
    isWatched = isWatched,
    comment = comment,
    userRating = userRating,
    imdbRating = imdbRating,
    externalIds = externalIds,
    watchProviders = watchProviders,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

fun Movie.toUpdateRequestDto(): UpdateMovieRequestDto = UpdateMovieRequestDto(
    title = title,
    description = description,
    releaseYear = releaseYear,
    categoryId = categoryId,
    posterUrl = posterUrl,
    runtimeMinutes = runtimeMinutes,
    isWatched = isWatched,
    comment = comment,
    userRating = userRating,
    imdbRating = imdbRating,
    externalIds = externalIds,
    watchProviders = watchProviders,
    updatedAtEpochMillis = updatedAtEpochMillis,
    version = version,
)

fun Movie.toCreateRequestDto(): CreateMovieRequestDto = CreateMovieRequestDto(
    title = title,
    description = description,
    releaseYear = releaseYear,
    categoryId = categoryId,
    posterUrl = posterUrl,
    runtimeMinutes = runtimeMinutes,
    isWatched = isWatched,
    comment = comment,
    userRating = userRating,
    imdbRating = imdbRating,
    externalIds = externalIds,
    watchProviders = watchProviders,
    updatedAtEpochMillis = updatedAtEpochMillis,
)