package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.FakeMovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMoviesSortedUseCaseTest {
    val movies = listOf(
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
        ),
        Movie(
            id = "3",
            title = "Test Movie 3",
            description = "Test Description 3",
            releaseYear = 2024,
            categoryId = "1",
            posterUrl = "https://example.com/poster1.jpg",
            runtimeMinutes = 80,
            isWatched = false,
            comment = "Test Comment 1",
            userRating = 7,
            imdbRating = 7.8,
            externalIds = mapOf("imdb" to "tt1234567"),
            watchProviders = listOf("Netflix"),
            updatedAtEpochMillis = 1_719_173_719L
        )
    )
    val repository = FakeMovieRepository(movies)
    val useCase = GetMoviesSortedUseCase(repository)

    @Test
    fun `invoke return sorted movies by title in ascending order`() = runTest  {

        // When Movies list is sorted by title in ascending order.
        val actualMovies = useCase(SortBy.TITLE, ascending = true)
        val ascendingList = movies.sortedBy { it.title }

        // Then the order should be "Test Movie 1", "Test Movie 2", "Test Movie 3"
        assertEquals(ascendingList[0].title, actualMovies[0].title)
        assertEquals(ascendingList[1].title, actualMovies[1].title)
        assertEquals(ascendingList[2].title, actualMovies[2].title)
    }

    @Test
    fun `invoke return sorted movies by title in descending order`() = runTest  {

        // When Movies list is sorted by title in descending order.
        val actualMovies = useCase(SortBy.TITLE, ascending = false)
        val descendingList = movies.sortedByDescending { it.title }
        // Then the order should be "Test Movie 3", "Test Movie 2", "Test Movie 1"
        assertEquals(descendingList[0].title, actualMovies[0].title)
        assertEquals(descendingList[1].title, actualMovies[1].title)
        assertEquals(descendingList[2].title, actualMovies[2].title)
    }

    @Test
    fun `invoke return sorted movies by releaseYear in ascending order`() = runTest  {

        // When Movies list is sorted by releaseYear in ascending order.
        val actualMovies = useCase(SortBy.RELEASE_YEAR, ascending = true)
        val ascendingList = movies.sortedBy { it.releaseYear }
        // Then the order should be "2020", "2022", "2024"
        assertEquals(ascendingList[0].releaseYear, actualMovies[0].releaseYear)
        assertEquals(ascendingList[1].releaseYear, actualMovies[1].releaseYear)
        assertEquals(ascendingList[2].releaseYear, actualMovies[2].releaseYear)
    }

    @Test
    fun `invoke return sorted movies by releaseYear in descending order`() = runTest  {

        // When Movies list is sorted by releaseYear in descending order.
        val actualMovies = useCase(SortBy.RELEASE_YEAR, ascending = false)
        val descendingList = movies.sortedByDescending { it.releaseYear }
        // Then the order should be "2024", "2022", "2020"
        assertEquals(descendingList[0].releaseYear, actualMovies[0].releaseYear)
        assertEquals(descendingList[1].releaseYear, actualMovies[1].releaseYear)
        assertEquals(descendingList[2].releaseYear, actualMovies[2].releaseYear)
    }

    @Test
    fun `invoke return sorted movies by userRating in ascending order`() = runTest  {

        // When Movies list is sorted by userRating in ascending order.
        val actualMovies = useCase(SortBy.RATING, ascending = true)
        val ascendingList = movies.sortedBy { it.userRating }
        // Then the order should be "4", "5", "7"
        assertEquals(ascendingList[0].userRating, actualMovies[0].userRating)
        assertEquals(ascendingList[1].userRating, actualMovies[1].userRating)
        assertEquals(ascendingList[2].userRating, actualMovies[2].userRating)
    }

    @Test
    fun `invoke return sorted movies by userRating in descending order`() = runTest  {

        // When Movies list is sorted by userRating in descending order.
        val actualMovies = useCase(SortBy.RATING, ascending = false)
        val descendingList = movies.sortedByDescending { it.userRating }
        // Then the order should be "2024", "2022", "2020"
        assertEquals(descendingList[0].userRating, actualMovies[0].userRating)
        assertEquals(descendingList[1].userRating, actualMovies[1].userRating)
        assertEquals(descendingList[2].userRating, actualMovies[2].userRating)
    }
}