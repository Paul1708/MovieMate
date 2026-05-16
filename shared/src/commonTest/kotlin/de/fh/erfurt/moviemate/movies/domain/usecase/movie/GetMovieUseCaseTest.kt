package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.FakeMovieRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetMovieUseCaseTest {
        val exceptedMovies = listOf(
            Movie(
                id = "1",
                title = "Test Movie 1",
                description = "Test Description 1",
                releaseYear = 2020,
                categoryId = "1",
                posterUrl = "https://example.com/poster1.jpg",
                runtimeMinutes = 120,
                isWatched = false,
                comment = "Test Comment 1",
                userRating = 4,
                imdbRating = 4.0,
                externalIds = mapOf("imdb" to "tt1234567"),
                watchProviders = listOf("Netflix"),
                updatedAtEpochMillis = 1_717_171_717L
            ),
            Movie(
                id = "2",
                title = "Test Movie 2",
                description = "Test Description 2",
                releaseYear = 2022,
                categoryId = "1",
                posterUrl = "https://example.com/poster1.jpg",
                runtimeMinutes = 60,
                isWatched = false,
                comment = "Test Comment 1",
                userRating = 5,
                imdbRating = 5.0,
                externalIds = mapOf("imdb" to "tt1234567"),
                watchProviders = listOf("Netflix"),
                updatedAtEpochMillis = 1_718_172_718L
            )
        )
        val repository = FakeMovieRepository(exceptedMovies)
        val useCase = GetMovieUseCase(repository)

    @Test
    fun `invoke returns the movie from the repository`() = runTest {
        // Given
        val actualMovie = useCase("1")
        // Then
        assertEquals( exceptedMovies[0],actualMovie)
    }

    @Test
    fun `invoke rejects a blank movieId`() = runTest  {

        // When movieId is blank
        val error = assertFailsWith<IllegalArgumentException> {
            useCase("   ")
        }
        // Then
        assertEquals( "Movie ID cannot be empty",error.message)

    }
}