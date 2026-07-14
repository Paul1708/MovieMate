package de.fh.erfurt.moviemate.movies.data.repository.remote

import de.fh.erfurt.moviemate.movies.data.mapper.remote.toFetchedMovieData
import de.fh.erfurt.moviemate.movies.data.remote.datasource.OmdbRemoteDataSource
import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieMetadataRepository

class OmdbMovieMetadataRepository(
    private val omdbRemoteDataSource: OmdbRemoteDataSource,
) : MovieMetadataRepository {
    override suspend fun fetchMovieDataByTitle(title: String): FetchedMovieData? {
        return omdbRemoteDataSource.fetchMovieDataByTitle(title)?.toFetchedMovieData()
    }
}
