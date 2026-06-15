package de.fh.erfurt.moviemate.movies.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BackupDataDto(
    val movies: List<MovieDto>,
    val categories: List<CategoryDto>
)
