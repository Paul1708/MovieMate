package de.fh.erfurt.moviemate.movies.data.repository

import de.fh.erfurt.moviemate.movies.data.remote.datasource.MovieRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.remote.datasource.CategoryRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.remote.dto.CreateMovieRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateMovieRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.CreateCategoryRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateCategoryRequestDto
import de.fh.erfurt.moviemate.movies.data.repository.remote.RemoteMovieRepository
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import kotlinx.coroutines.test.runTest
import kotlin.collections.listOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RemoteMovieRepositoryTest {

    @Test
    fun `getMovies maps remote DTOs into domain models`() = runTest {
        val remoteMovieRepository = RemoteMovieRepository(
            remoteDataSource = FakeMovieRemoteDataSource(
                fetchedMovies = listOf(
                    MovieDto(
                        id = "1",
                        title = "Movie 1",
                        description = "Description 1",
                        releaseYear = 2020,
                        categoryId = "cat1",
                        posterUrl = "https://example.com/poster1.jpg",
                        runtimeMinutes = 120,
                        isWatched = true,
                        comment = "Great movie!",
                        userRating = 5,
                        imdbRating = 8.0,
                        externalIds = mapOf("imdb" to "tt1234567"),
                        watchProviders = listOf("Netflix"),
                        updatedAtEpochMillis = 1_717_171_725L,
                        version = 1L
                    )
                )
            ),
            categoryRemoteDataSource = FakeCategoryRemoteDataSource()
        )

        val movies = remoteMovieRepository.getMovies()

        assertEquals(
            expected = listOf(
                Movie(
                    id = "1",
                    title = "Movie 1",
                    description = "Description 1",
                    releaseYear = 2020,
                    categoryId = "cat1",
                    posterUrl = "https://example.com/poster1.jpg",
                    runtimeMinutes = 120,
                    isWatched = true,
                    comment = "Great movie!",
                    userRating = 5,
                    imdbRating = 8.0,
                    externalIds = mapOf("imdb" to "tt1234567"),
                    watchProviders = listOf("Netflix"),
                    updatedAtEpochMillis = 1_717_171_725L,
                    version = 1L
                )
            ),
            actual = movies
        )
    }

    @Test
    fun `addMovie maps the domain draft into a create request dto`() = runTest {
        val remoteMovieRepository = FakeMovieRemoteDataSource(
            fetchedMovies = listOf(
                MovieDto(
                    id = "2",
                    title = "Movie 2",
                    description = "Description 2",
                    releaseYear = 2020,
                    categoryId = "cat1",
                    posterUrl = "https://example.com/poster1.jpg",
                    runtimeMinutes = 120,
                    isWatched = true,
                    comment = "Repository uses the remote data source.",
                    userRating = 5,
                    imdbRating = 8.0,
                    externalIds = mapOf("imdb" to "tt1234567"),
                    watchProviders = listOf("Netflix"),
                    updatedAtEpochMillis = 1_717_171_726L,
                    version = 1L
                )
            )
        )
        val repository = RemoteMovieRepository(
            remoteDataSource = remoteMovieRepository,
            categoryRemoteDataSource = FakeCategoryRemoteDataSource()
        )
        val createMovie = repository.addMovie(
            MovieDraft(
                title = "New Movie",
                description = "A new movie to be created",
                releaseYear = 2021,
                categoryId = "cat2",
                posterUrl = "https://example.com/poster2.jpg",
                runtimeMinutes = 90,
                isWatched = false,
                comment = null,
                userRating = null,
                imdbRating = null,
                externalIds = null,
                watchProviders = null,
                updatedAtEpochMillis = 1_717_171_726L
            )
        )

        assertEquals(
            expected = CreateMovieRequestDto(
                title = "New Movie",
                description = "A new movie to be created",
                releaseYear = 2021,
                categoryId = "cat2",
                posterUrl = "https://example.com/poster2.jpg",
                runtimeMinutes = 90,
                isWatched = false,
                comment = null,
                userRating = null,
                imdbRating = null,
                externalIds = null,
                watchProviders = null,
                updatedAtEpochMillis = 1_717_171_726L
            ),
            actual = remoteMovieRepository.lastCreateRequest
        )

        assertEquals(
            expected = Movie(
                id = "created-movie",
                title = "Created by fake",
                description = "Fake remote response",
                releaseYear = 2020,
                categoryId = "cat1",
                posterUrl = "https://example.com/poster1.jpg",
                runtimeMinutes = 120,
                isWatched = true,
                comment = "Great movie!",
                userRating = 5,
                imdbRating = 8.0,
                externalIds = mapOf("imdb" to "tt1234567"),
                watchProviders = listOf("Netflix"),
                updatedAtEpochMillis = 1620000000000,
                version = 1L
            ),
            actual = createMovie
        )
    }

    @Test
    fun `updateMovie maps the domain model into an update request dto`() = runTest {
        val remoteMovieRepository = FakeMovieRemoteDataSource()
        val repository = RemoteMovieRepository(
            remoteDataSource = remoteMovieRepository,
            categoryRemoteDataSource = FakeCategoryRemoteDataSource()
        )
        val updatedMovie = repository.updateMovie(
            Movie(
                id = "movie-id",
                title = "Updated Movie",
                description = "An existing movie to be updated",
                releaseYear = 2021,
                categoryId = "cat2",
                posterUrl = "https://example.com/poster2.jpg",
                runtimeMinutes = 90,
                isWatched = false,
                comment = null,
                userRating = null,
                imdbRating = null,
                externalIds = null,
                watchProviders = null,
                updatedAtEpochMillis = 1_717_171_726L
            )
        )

        assertEquals(
            expected = UpdateMovieRequestDto(
                title = "Updated Movie",
                description = "An existing movie to be updated",
                releaseYear = 2021,
                categoryId = "cat2",
                posterUrl = "https://example.com/poster2.jpg",
                runtimeMinutes = 90,
                isWatched = false,
                comment = null,
                userRating = null,
                imdbRating = null,
                externalIds = null,
                watchProviders = null,
                updatedAtEpochMillis = 1_717_171_726L
            ),
            actual = remoteMovieRepository.lastUpdateRequest
        )

        assertEquals(
            expected = "movie-id",
            actual = remoteMovieRepository.lastUpdatedMovieId
        )

        assertEquals(
            expected = Movie(
                id = "updated-movie",
                title = "Updated by fake",
                description = "Fake remote response",
                releaseYear = 2020,
                categoryId = "cat1",
                posterUrl = "https://example.com/poster1.jpg",
                runtimeMinutes = 120,
                isWatched = true,
                comment = "Update movie!",
                userRating = 5,
                imdbRating = 8.0,
                externalIds = mapOf("imdb" to "tt1234567"),
                watchProviders = listOf("Netflix"),
                updatedAtEpochMillis = 1620000000000,
                version = 1L
            ),
            actual = updatedMovie
        )
    }

    @Test
    fun `deleteMovie calls the remote data source with the correct movie id`() = runTest {
        val remoteMovieRepository = FakeMovieRemoteDataSource()
        val repository = RemoteMovieRepository(
            remoteDataSource = remoteMovieRepository,
            categoryRemoteDataSource = FakeCategoryRemoteDataSource()
        )
        repository.deleteMovie("movie-id-to-delete")

        assertEquals(
            expected = "movie-id-to-delete",
            actual = remoteMovieRepository.lastDeleteMovieId
        )
        assertNull(remoteMovieRepository.lastUpdateRequest)
    }

    @Test
    fun `searchMovies maps remote DTOs into domain models`() = runTest {
        val remoteMovieRepository = RemoteMovieRepository(
            remoteDataSource = FakeMovieRemoteDataSource(
                fetchedMovies = listOf(
                    MovieDto(
                        id = "1",
                        title = "Action Movie",
                        description = "An action-packed movie",
                        releaseYear = 2020,
                        categoryId = "cat1",
                        posterUrl = "https://example.com/poster1.jpg",
                        runtimeMinutes = 120,
                        isWatched = true,
                        comment = "Great action scenes!",
                        userRating = 5,
                        imdbRating = 8.0,
                        externalIds = mapOf("imdb" to "tt1234567"),
                        watchProviders = listOf("Netflix"),
                        updatedAtEpochMillis = 1_717_171_725L,
                        version = 1L
                    ),
                    MovieDto(
                        id = "2",
                        title = "Romantic Comedy",
                        description = "A heartwarming romantic comedy",
                        releaseYear = 2021,
                        categoryId = "cat2",
                        posterUrl = "https://example.com/poster2.jpg",
                        runtimeMinutes = 90,
                        isWatched = false,
                        comment = "Funny and romantic!",
                        userRating = 4,
                        imdbRating = 7.5,
                        externalIds = mapOf("imdb" to "tt7654321"),
                        watchProviders = listOf("Amazon Prime"),
                        updatedAtEpochMillis = 1_717_171_726L,
                        version = 1L
                    )
                )
            ),
            categoryRemoteDataSource = FakeCategoryRemoteDataSource()
        )

        val searchResults = remoteMovieRepository.searchMovies("action")

        assertEquals(
            expected = listOf(
                Movie(
                    id = "1",
                    title = "Action Movie",
                    description = "An action-packed movie",
                    releaseYear = 2020,
                    categoryId = "cat1",
                    posterUrl = "https://example.com/poster1.jpg",
                    runtimeMinutes = 120,
                    isWatched = true,
                    comment = "Great action scenes!",
                    userRating = 5,
                    imdbRating = 8.0,
                    externalIds = mapOf("imdb" to "tt1234567"),
                    watchProviders = listOf("Netflix"),
                    updatedAtEpochMillis = 1_717_171_725L,
                    version = 1L
                )
            ),
            actual = searchResults
        )
    }

    @Test
    fun `getMoviesSorted maps remote DTOs into domain models and sorts them correctly`() = runTest {
        val remoteMovieRepository = RemoteMovieRepository(
            remoteDataSource = FakeMovieRemoteDataSource(
                fetchedMovies = listOf(
                    MovieDto(
                        id = "1",
                        title = "B Movie",
                        description = "Description B",
                        releaseYear = 2020,
                        categoryId = "cat1",
                        posterUrl = "https://example.com/poster1.jpg",
                        runtimeMinutes = 120,
                        isWatched = true,
                        comment = "Great movie!",
                        userRating = 5,
                        imdbRating = 8.0,
                        externalIds = mapOf("imdb" to "tt1234567"),
                        watchProviders = listOf("Netflix"),
                        updatedAtEpochMillis = 1_717_171_725L,
                        version = 1L
                    ),
                    MovieDto(
                        id = "2",
                        title = "A Movie",
                        description = "Description A",
                        releaseYear = 2021,
                        categoryId = "cat2",
                        posterUrl = "https://example.com/poster2.jpg",
                        runtimeMinutes = 90,
                        isWatched = false,
                        comment = "Funny and romantic!",
                        userRating = 4,
                        imdbRating = 7.5,
                        externalIds = mapOf("imdb" to "tt7654321"),
                        watchProviders = listOf("Amazon Prime"),
                        updatedAtEpochMillis = 1_717_171_726L,
                        version = 1L
                    )
                )
            ),
            categoryRemoteDataSource = FakeCategoryRemoteDataSource()
        )

        val sortedMoviesAsc = remoteMovieRepository.getMoviesSorted(SortBy.TITLE, ascending = true)

        assertEquals(
            expected = listOf(
                Movie(
                    id = "2",
                    title = "A Movie",
                    description = "Description A",
                    releaseYear = 2021,
                    categoryId = "cat2",
                    posterUrl = "https://example.com/poster2.jpg",
                    runtimeMinutes = 90,
                    isWatched = false,
                    comment = "Funny and romantic!",
                    userRating = 4,
                    imdbRating = 7.5,
                    externalIds = mapOf("imdb" to "tt7654321"),
                    watchProviders = listOf("Amazon Prime"),
                    updatedAtEpochMillis = 1_717_171_726L,
                    version = 1L
                ),
                Movie(
                    id = "1",
                    title = "B Movie",
                    description = "Description B",
                    releaseYear = 2020,
                    categoryId = "cat1",
                    posterUrl = "https://example.com/poster1.jpg",
                    runtimeMinutes = 120,
                    isWatched = true,
                    comment = "Great movie!",
                    userRating = 5,
                    imdbRating = 8.0,
                    externalIds = mapOf("imdb" to "tt1234567"),
                    watchProviders = listOf("Netflix"),
                    updatedAtEpochMillis = 1_717_171_725L,
                    version = 1L
                )
            ),
            actual = sortedMoviesAsc
        )
    }

    @Test
    fun `getMoviesByCategory maps remote DTOs in domain models and sorts them correctly`() = runTest {
        val remoteMovieRepository = RemoteMovieRepository(
            remoteDataSource = FakeMovieRemoteDataSource(
                fetchedMovies = listOf(
                    MovieDto(
                        id = "1",
                        title = "B Movie",
                        description = "Description B",
                        releaseYear = 2020,
                        categoryId = "10355",
                        posterUrl = "https://example.com/poster1.jpg",
                        runtimeMinutes = 120,
                        isWatched = true,
                        comment = "Great movie!",
                        userRating = 5,
                        imdbRating = 7.5,
                        externalIds = mapOf("imdb" to "tt1234567"),
                        watchProviders = listOf("Amazon Prime"),
                        updatedAtEpochMillis = 1_717_171_726L,
                        version = 1L
                    )
                )
            ),
            categoryRemoteDataSource = FakeCategoryRemoteDataSource()
        )

        val movie = remoteMovieRepository.getMoviesByCategory("10355")

        assertEquals(
            expected = listOf(
                Movie(
                    id = "1",
                    title = "B Movie",
                    description = "Description B",
                    releaseYear = 2020,
                    categoryId = "10355",
                    posterUrl = "https://example.com/poster1.jpg",
                    runtimeMinutes = 120,
                    isWatched = true,
                    comment = "Great movie!",
                    userRating = 5,
                    imdbRating = 7.5,
                    externalIds = mapOf("imdb" to "tt1234567"),
                    watchProviders = listOf("Amazon Prime"),
                    updatedAtEpochMillis = 1_717_171_726L,
                    version = 1L
                )
            ),
            actual = movie
        )
    }
}

private class FakeCategoryRemoteDataSource : CategoryRemoteDataSource {
    override suspend fun fetchCategories(): List<CategoryDto> = emptyList()
    override suspend fun fetchCategory(categoryId: String): CategoryDto? = null
    override suspend fun createCategory(request: CreateCategoryRequestDto): CategoryDto =
        CategoryDto(
            id = "id",
            name = request.name,
            iconKey = request.iconKey,
            updatedAtEpochMillis = request.updatedAtEpochMillis,
        )
    override suspend fun updateCategory(categoryId: String, request: UpdateCategoryRequestDto): CategoryDto =
        CategoryDto(
            id = categoryId,
            name = request.name,
            iconKey = request.iconKey,
            updatedAtEpochMillis = request.updatedAtEpochMillis,
        )
    override suspend fun deleteCategory(categoryId: String) {}
}

private class FakeMovieRemoteDataSource(
    private val fetchedMovies: List<MovieDto> = emptyList(),
    private val createdMovie: MovieDto = MovieDto(
        id = "created-movie",
        title = "Created by fake",
        description = "Fake remote response",
        releaseYear = 2020,
        categoryId = "cat1",
        posterUrl = "https://example.com/poster1.jpg",
        runtimeMinutes = 120,
        isWatched = true,
        comment = "Great movie!",
        userRating = 5,
        imdbRating = 8.0,
        externalIds = mapOf("imdb" to "tt1234567"),
        watchProviders = listOf("Netflix"),
        updatedAtEpochMillis = 1620000000000,
        version = 1L
    ),
    private val updatedMovie: MovieDto = MovieDto(
        id = "updated-movie",
        title = "Updated by fake",
        description = "Fake remote response",
        releaseYear = 2020,
        categoryId = "cat1",
        posterUrl = "https://example.com/poster1.jpg",
        runtimeMinutes = 120,
        isWatched = true,
        comment = "Update movie!",
        userRating = 5,
        imdbRating = 8.0,
        externalIds = mapOf("imdb" to "tt1234567"),
        watchProviders = listOf("Netflix"),
        updatedAtEpochMillis = 1620000000000,
        version = 1L
    )
) : MovieRemoteDataSource {
    var lastCreateRequest: CreateMovieRequestDto? = null
        private set
    var lastUpdateRequest: UpdateMovieRequestDto? = null
        private set
    var lastUpdatedMovieId: String? = null
        private set
    var lastDeleteMovieId: String? = null
        private set

    override suspend fun fetchMovies(): List<MovieDto> {
        return fetchedMovies
    }

    override suspend fun fetchMovie(movieId: String): MovieDto? {
        return fetchedMovies.find { it.id == movieId }
    }

    override suspend fun createMovie(request: CreateMovieRequestDto): MovieDto {
        lastCreateRequest = request
        return createdMovie
    }

    override suspend fun updateMovie(movieId: String, request: UpdateMovieRequestDto): MovieDto {
        lastUpdateRequest = request
        lastUpdatedMovieId = movieId
        return updatedMovie
    }

    override suspend fun deleteMovie(movieId: String) {
        lastDeleteMovieId = movieId
    }

    override suspend fun searchMovies(query: String): List<MovieDto> {
        return fetchedMovies.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    }

    override suspend fun fetchMoviesByCategory(categoryId: String): List<MovieDto> {
        return fetchedMovies.filter { it.categoryId == categoryId }
    }

    override suspend fun fetchMoviesSorted(
        sortBy: SortBy,
        ascending: Boolean
    ): List<MovieDto> {
        val sorted = when (sortBy) {
            SortBy.TITLE -> fetchedMovies.sortedBy { it.title }
            SortBy.RELEASE_YEAR -> fetchedMovies.sortedBy { it.releaseYear }
            SortBy.RATING -> fetchedMovies.sortedBy { it.userRating }
            else -> {fetchedMovies }
        }
        return if (ascending) sorted else sorted.reversed()
    }
}
