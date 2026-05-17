package de.fh.erfurt.moviemate.movies.data.repository.remote

import de.fh.erfurt.moviemate.movies.data.mapper.remote.toCreateRequestDto
import de.fh.erfurt.moviemate.movies.data.mapper.remote.toDomainModel
import de.fh.erfurt.moviemate.movies.data.mapper.remote.toUpdateRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.datasource.MovieRemoteDataSource
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy

class RemoteMovieRepository(
    private val remoteDataSource: MovieRemoteDataSource,
    ): MovieRepository {
    override suspend fun getMovies(): List<Movie> {
        return remoteDataSource.fetchMovies().map { dto -> dto.toDomainModel() }
    }

    override suspend fun getMovie(movieId: String): Movie? {
        return remoteDataSource.fetchMovie(movieId)?.toDomainModel()
    }

    override suspend fun addMovie(draft: MovieDraft): Movie {
        return remoteDataSource.createMovie(draft.toCreateRequestDto()).toDomainModel()
    }

    override suspend fun updateMovie(movie: Movie): Movie {
        return remoteDataSource.updateMovie(movie.id, movie.toUpdateRequestDto()).toDomainModel()
    }

    override suspend fun deleteMovie(movieId: String) {
        remoteDataSource.deleteMovie(movieId)
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return remoteDataSource.searchMovies(query).map { dto -> dto.toDomainModel() }
    }

    override suspend fun getMoviesByCategory(categoryId: String): List<Movie> {
        return remoteDataSource.fetchMoviesByCategory(categoryId).map { dto -> dto.toDomainModel() }
    }

    override suspend fun getMoviesSorted(sortBy: SortBy, ascending: Boolean): List<Movie> {
        return remoteDataSource.fetchMoviesSorted(sortBy = sortBy, ascending = ascending)
            .map { dto -> dto.toDomainModel() }
    }
}