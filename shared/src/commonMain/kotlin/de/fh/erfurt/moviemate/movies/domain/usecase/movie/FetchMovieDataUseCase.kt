package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieSearchResults
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieMetadataRepository

class FetchMovieDataUseCase(
    private val movieMetadataRepository: MovieMetadataRepository,
) {
    suspend fun search(title: String, releaseYear: String): MovieSearchResults {
        val query = title.trim()
        require(query.isNotEmpty()) { "Please enter a movie title first." }
        val year = releaseYear.trim().takeUnless(String::isEmpty)
        require(year == null || year.matches(ReleaseYear)) { "Enter a four-digit year or leave it blank." }
        return movieMetadataRepository.searchMovies(query, year)
    }

    suspend fun byImdbId(imdbId: String): FetchedMovieData? {
        require(imdbId.matches(ImdbId)) { "Invalid IMDb ID." }
        return movieMetadataRepository.fetchMovieDataByImdbId(imdbId)
    }
}

private val ReleaseYear = Regex("[0-9]{4}")
internal val ImdbId = Regex("tt[0-9]{7,}")
