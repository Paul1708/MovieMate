package de.fh.erfurt.moviemate.movies.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OmdbMovieDto(
    @SerialName("Title")
    val title: String? = null,

    @SerialName("Plot")
    val description: String? = null,

    @SerialName("Year")
    val year: String? = null,

    @SerialName("Poster")
    val poster: String? = null,

    @SerialName("Runtime")
    val runtime: String? = null,

    @SerialName("Genre")
    val genre: String? = null,

    @SerialName("imdbRating")
    val imdbRating: String? = null,

    @SerialName("imdbID")
    val imdbId: String? = null,

    @SerialName("Response")
    val response: String? = null,

    @SerialName("Error")
    val error: String? = null,
)

@Serializable
data class OmdbSearchResponse(
    @SerialName("Search") val matches: List<OmdbSearchMatchDto> = emptyList(),
    val totalResults: String? = null,
    @SerialName("Response") val response: String? = null,
    @SerialName("Error") val error: String? = null,
)

@Serializable
data class OmdbSearchMatchDto(
    @SerialName("imdbID") val imdbId: String? = null,
    @SerialName("Title") val title: String? = null,
    @SerialName("Year") val year: String? = null,
    @SerialName("Poster") val poster: String? = null,
)
