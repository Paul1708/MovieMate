package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.FakeMovieRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UpdateMovieUseCaseTest {

    val movies = listOf(
        Movie(
            id = "1",
            title = "Movie_1",
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
            title = "Movie_2",
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
    val repository = FakeMovieRepository(movies)
    val useCase = UpdateMovieUseCase(repository)

    @Test
    fun `invoke returns updated movie from repository `() = runTest  {

        // Given
        val updatedMovie = movies[0].copy(title = "Updated Movie Title")
        //When
        val actualMovie = useCase(updatedMovie)
        //Than
        assertNotNull(actualMovie)
        assertEquals(updatedMovie, actualMovie)
    }

    @Test
    fun `invoke rejects a blank id`() = runTest {
        val error = assertFailsWith<IllegalArgumentException> {
            val updatedMovie = movies[0].copy(id = "  ")
            useCase(
                updatedMovie
            )
        }
        assertEquals( "Movie ID cannot be empty",error.message)

    }

    @Test
    fun `invoke rejects a blank title`() = runTest {
        val error = assertFailsWith<IllegalArgumentException> {
            val updatedMovie = movies[0].copy(title = "  ")
            useCase(
                updatedMovie
            )
        }
        assertEquals( "Title cannot be empty",error.message)

    }

    @Test
    fun `invoke clears the existing description`() = runTest {
        val updatedMovie = useCase(movies[0].copy(description = "  "))

        assertEquals("", updatedMovie.description)
    }

    @Test
    fun `invoke rejects a negative runtimeMinutes`() = runTest {
        val error = assertFailsWith<IllegalArgumentException> {
            val updatedMovie = movies[0].copy(runtimeMinutes = -10)
            useCase(
                updatedMovie
            )
        }
        assertEquals( "Runtime must be positive",error.message)

    }

    @Test
    fun `invoke rejects a userrating greater than 10`() = runTest {
        val error = assertFailsWith<IllegalArgumentException> {
            val updatedMovie = movies[0].copy(userRating = 15)
            useCase(
                updatedMovie
            )
        }
        assertEquals( "User rating must be between 1 and 10",error.message)

    }

    @Test
    fun `invoke rejects a imdbRating less than 0`() = runTest {
        val error = assertFailsWith<IllegalArgumentException> {
            val updatedMovie = movies[0].copy(imdbRating = -1.0)
            useCase(
                updatedMovie
            )
        }
        assertEquals( "IMDb rating must be between 0.0 and 10.0",error.message)

    }


}