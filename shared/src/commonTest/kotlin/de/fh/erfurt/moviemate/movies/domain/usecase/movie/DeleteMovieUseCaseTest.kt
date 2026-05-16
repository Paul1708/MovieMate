package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.FakeMovieRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeleteMovieUseCaseTest {

        val movies = listOf(
            Movie(
                id = "1",
                title = "Test Movie 1",
                description = "Test Description 1",
                releaseYear = 2024,
                categoryId = "1",
                posterUrl = "https://example.com/poster1.jpg",
                runtimeMinutes = 120,
                isWatched = false,
                comment = "Test Comment 1",
                userRating = 7,
                imdbRating = 7.8,
                externalIds = mapOf("imdb" to "tt1234567"),
                watchProviders = listOf("Netflix"),
                updatedAtEpochMillis = 1_717_171_717L
            ),
            Movie(
                id = "2",
                title = "Test Movie 2",
                description = "Test Description 2",
                releaseYear = 2024,
                categoryId = "2",
                posterUrl = "https://example.com/poster2.jpg",
                runtimeMinutes = 130,
                isWatched = true,
                comment = "Test Comment 2",
                userRating = 8,
                imdbRating = 8.5,
                externalIds = mapOf("imdb" to "tt7654321"),
                watchProviders = listOf("Amazon Prime"),
                updatedAtEpochMillis = 1_717_171_717L
            )
        )
        val repository = FakeMovieRepository(movies)
        val useCase = DeleteMovieUseCase(repository)

    @Test
    fun `invoke delete movie from the repository`() = runTest  {

        // When delete movie with id "1"
        useCase("1")
        // Then
        assertEquals(listOf(movies[1]), repository.getMovies())
    }

    @Test
    fun `invoke rejects a blank movieId`() = runTest  {

        // When delete movie with blank id
        val error = assertFailsWith<IllegalArgumentException> {
            useCase("   ")
        }
        // Then
        assertEquals( "Movie ID cannot be empty",error.message)

    }
}