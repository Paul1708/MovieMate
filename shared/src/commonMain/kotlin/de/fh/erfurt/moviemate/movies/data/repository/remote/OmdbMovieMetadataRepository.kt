package de.fh.erfurt.moviemate.movies.data.repository.remote

import de.fh.erfurt.moviemate.movies.data.mapper.remote.toFetchedMovieData
import de.fh.erfurt.moviemate.movies.data.remote.datasource.OmdbRemoteDataSource
import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieSearchMatch
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieSearchResults
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieMetadataRepository
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.ImdbId

class OmdbMovieMetadataRepository(
    private val omdbRemoteDataSource: OmdbRemoteDataSource,
) : MovieMetadataRepository {
    override suspend fun searchMovies(title: String, year: String?): MovieSearchResults {
        val response = omdbRemoteDataSource.searchMovies(title, year)
        val matches = response.matches.mapNotNull { match ->
            val imdbId = match.imdbId?.takeIf(ImdbId::matches) ?: return@mapNotNull null
            val name = match.title?.takeIf(String::isNotBlank) ?: return@mapNotNull null
            MovieSearchMatch(
                imdbId = imdbId,
                title = name,
                year = match.year.orEmpty(),
                posterUrl = match.poster?.takeIf { it.startsWith("https://") },
            )
        }.distinctBy(MovieSearchMatch::imdbId)
        val totalResults = response.totalResults?.toIntOrNull() ?: response.matches.size
        return MovieSearchResults(matches, maxOf(matches.size, totalResults))
    }

    override suspend fun fetchMovieDataByImdbId(imdbId: String): FetchedMovieData? {
        return omdbRemoteDataSource.fetchMovieDataByImdbId(imdbId)?.toFetchedMovieData()
    }
}
