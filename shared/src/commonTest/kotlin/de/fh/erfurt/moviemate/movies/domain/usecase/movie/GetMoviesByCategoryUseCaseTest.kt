package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.FakeMovieRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetMoviesByCategoryUseCaseTest {

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
        )
    )
    val repository = FakeMovieRepository(movies)
    val useCase = GetMoviesByCategoryUseCase(repository)

    @Test
    fun `invoke return movie from the repository by categoryId`() = runTest {
        //When
        val actualMovies = useCase("1")
        //Then
        assertEquals(movies[0], actualMovies[0])
    }

    @Test
    fun `invoke rejects a blank categoryId`() = runTest  {

        // When delete movie with blank id
        val error = assertFailsWith<IllegalArgumentException> {
            useCase("   ")
        }
        // Then
        assertEquals( "Category ID cannot be empty",error.message)
    }
}