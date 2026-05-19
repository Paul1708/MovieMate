package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.dto.CreateMovieRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateMovieRequestDto
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy

interface MovieRemoteDataSource {
    suspend fun fetchMovies(): List<MovieDto>

    suspend fun fetchMovie(movieId: String): MovieDto?

    suspend fun createMovie(request: CreateMovieRequestDto): MovieDto

    suspend fun updateMovie(movieId: String, request: UpdateMovieRequestDto): MovieDto

    suspend fun deleteMovie(movieId: String)

    suspend fun searchMovies(query: String): List<MovieDto>

    suspend fun fetchMoviesByCategory(categoryId: String): List<MovieDto>

    suspend fun fetchMoviesSorted(sortBy: SortBy, ascending: Boolean = true): List<MovieDto>
}