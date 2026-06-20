package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.FakeMovieRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchMoviesUseCaseTest {

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
    val useCase = SearchMoviesUseCase(repository)

        @Test
        fun `invoke returns the movies from the repository that match the query`() = runTest {
            // Given
            val query = "Movie_1"
            // When
            val actualMovies = useCase(query)
            // Then
            assertEquals(listOf(movies[0]), actualMovies)
        }

        @Test
        fun `invoke returns an empty list if no movies match the query`() = runTest {
            // Given
            val query = "Nonexistent"
            // When
            val actualMovies = useCase(query)
            // Then
            assertTrue(actualMovies.isEmpty())
        }
}