package de.fh.erfurt.moviemate.data.remote

import de.fh.erfurt.moviemate.data.remote.dto.CreateMovieRequestDto
import de.fh.erfurt.moviemate.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.data.remote.dto.UpdateMovieRequestDto
import de.fh.erfurt.moviemate.data.remote.dto.UpdateRatingRequestDto
import de.fh.erfurt.moviemate.data.remote.dto.UpdateWatchStatusRequestDto

interface MovieRemoteDataSource {
    suspend fun fetchMovies(): List<MovieDto>
    suspend fun createMovie(request: CreateMovieRequestDto): MovieDto
    suspend fun updateMovie(id: String, request: UpdateMovieRequestDto): MovieDto
    suspend fun deleteMovie(id: String)
    suspend fun updateWatchStatus(id: String, request: UpdateWatchStatusRequestDto): MovieDto
    suspend fun updateRating(id: String, request: UpdateRatingRequestDto): MovieDto
}