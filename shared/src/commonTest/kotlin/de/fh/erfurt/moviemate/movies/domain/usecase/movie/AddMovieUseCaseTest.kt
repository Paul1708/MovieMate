package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.FakeMovieRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AddMovieUseCaseTest {
    private val repository = FakeMovieRepository()
    private val useCase = AddMovieUseCase(repository)

    @Test
    fun `invoke returns the add movie from the repository`() = runTest {
        // Given
        val createdMovie = useCase(
            draft = MovieDraft(
                title = "Test Movie",
                description = "Test Description",
                releaseYear = 2024,
                categoryId = "1",
                posterUrl = "https://example.com/poster.jpg",
                runtimeMinutes = 120,
                isWatched = false,
                comment = "Test Comment",
                userRating = 7,
                imdbRating = 7.8,
                externalIds = mapOf("imdb" to "tt1234567"),
                watchProviders = listOf("Netflix"),
                updatedAtEpochMillis = 1_717_171_717L
            )
        )

         // Then
        assertEquals( "Test Movie",createdMovie.title)
        assertEquals(repository.getMovies(),listOf(createdMovie))
    }

    @Test
    fun `invoke rejects a blank title`() = runTest {
        val error = assertFailsWith<IllegalArgumentException> {
            useCase(
                draft = MovieDraft(
                    title = " ",
                    description = "Test Description",
                    releaseYear = 2024,
                    categoryId = "2",
                    posterUrl = "https://example.com/poster.jpg",
                    runtimeMinutes = 120,
                    isWatched = false,
                    comment = "Test Comment",
                    userRating = 7,
                    imdbRating = 7.8,
                    externalIds = mapOf("imdb" to "tt1234567"),
                    watchProviders = listOf("Netflix"),
                    updatedAtEpochMillis = 1_717_171_717L
                )
            )
        }
        assertEquals("Title cannot be empty",error.message)

    }

    @Test
    fun `invoke rejects a blank description`() = runTest {
        val error = assertFailsWith<IllegalArgumentException> {
            useCase(
                draft = MovieDraft(
                    title = "Test Movie",
                    description = " ",
                    releaseYear = 2024,
                    categoryId = "3",
                    posterUrl = "https://example.com/poster.jpg",
                    runtimeMinutes = 120,
                    isWatched = false,
                    comment = "Test Comment",
                    userRating = 7,
                    imdbRating = 7.8,
                    externalIds = mapOf("imdb" to "tt1234567"),
                    watchProviders = listOf("Amazon Prime"),
                    updatedAtEpochMillis = 1_717_171_717L
                )
            )
        }
        assertEquals( "Description cannot be empty",error.message)
    }

    @Test
    fun `invoke rejects a negative runtimeMinutes`() = runTest {
        val error = assertFailsWith<IllegalArgumentException> {
            useCase(
                draft = MovieDraft(
                    title = "Test Movie",
                    description = "Test Description",
                    releaseYear = 2024,
                    categoryId = "4",
                    posterUrl = "https://example.com/poster.jpg",
                    runtimeMinutes = -120,
                    isWatched = false,
                    comment = "Test Comment",
                    userRating = 7,
                    imdbRating = 7.8,
                    externalIds = mapOf("imdb" to "tt1234567"),
                    watchProviders = listOf("Amazon Prime"),
                    updatedAtEpochMillis = 1_717_171_717L
                )
            )
        }
        assertEquals( "Runtime must be positive",error.message)
    }

    @Test
    fun `invoke rejects a userrating greater than 10`() = runTest {
        val error = assertFailsWith<IllegalArgumentException> {
            useCase(
                draft = MovieDraft(
                    title = "Test Movie",
                    description = "Test Description",
                    releaseYear = 2024,
                    categoryId = "4",
                    posterUrl = "https://example.com/poster.jpg",
                    runtimeMinutes = 120,
                    isWatched = false,
                    comment = "Test Comment",
                    userRating = 12,
                    imdbRating = 7.8,
                    externalIds = mapOf("imdb" to "tt1234567"),
                    watchProviders = listOf("Amazon Prime"),
                    updatedAtEpochMillis = 1_717_171_717L
                )
            )
        }
        assertEquals( "User rating must be between 1 and 10",error.message)
    }

    @Test
    fun `invoke rejects a imdbRating less than 0`() = runTest {
        val error = assertFailsWith<IllegalArgumentException> {
            useCase(
                draft = MovieDraft(
                    title = "Test Movie",
                    description = "Test Description",
                    releaseYear = 2024,
                    categoryId = "4",
                    posterUrl = "https://example.com/poster.jpg",
                    runtimeMinutes = 120,
                    isWatched = false,
                    comment = "Test Comment",
                    userRating = 2,
                    imdbRating = -7.8,
                    externalIds = mapOf("imdb" to "tt1234567"),
                    watchProviders = listOf("Amazon Prime"),
                    updatedAtEpochMillis = 1_717_171_717L
                )
            )
        }
        assertEquals( "IMDb rating must be between 0.0 and 10.0",error.message)
    }
}