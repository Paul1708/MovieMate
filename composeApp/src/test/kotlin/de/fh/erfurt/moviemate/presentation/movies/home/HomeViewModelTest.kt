package de.fh.erfurt.moviemate.presentation.movies.home

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.DeleteMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var movieRepository: InMemoryMovieRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        movieRepository = InMemoryMovieRepository(
            mutableListOf(
                movie(id = "1", title = "Alien", updatedAt = 10),
                movie(id = "2", title = "Severance", updatedAt = 20).copy(
                    mediaType = MediaType.SERIES,
                    runtimeMinutes = null,
                    totalSeasons = 2,
                ),
            )
        )
        val categoryRepository = InMemoryCategoryRepository(
            listOf(Category(id = "science-fiction", name = "Science Fiction", updatedAtEpochMillis = 1))
        )
        viewModel = HomeViewModel(
            getCategoriesUseCase = GetCategoriesUseCase(categoryRepository),
            getMoviesUseCase = GetMoviesUseCase(movieRepository),
            deleteMovieUseCase = DeleteMovieUseCase(movieRepository),
            ioDispatcher = dispatcher,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadAndSearchUpdateRenderState() = runTest(dispatcher) {
        viewModel.onAction(HomeUiAction.LoadRequested())

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(listOf("Severance", "Alien"), viewModel.uiState.value.movies.map { it.title })

        viewModel.onAction(HomeUiAction.SearchChanged("Alien"))

        assertEquals(listOf("Alien"), viewModel.uiState.value.movies.map { it.title })
    }

    @Test
    fun mediaTypeFilterCombinesWithWatchlistAndResets() = runTest(dispatcher) {
        viewModel.onAction(HomeUiAction.LoadRequested())
        viewModel.onAction(HomeUiAction.TabSelected(HomeTab.WatchLater))
        viewModel.onAction(HomeUiAction.MediaTypeSelected(MediaType.SERIES))

        val series = viewModel.uiState.value.movies.single()
        assertEquals("Severance", series.title)
        assertEquals("Series · 2 seasons", series.runtimeText)

        viewModel.onAction(HomeUiAction.ResetFiltersClicked)
        assertEquals(listOf("Severance", "Alien"), viewModel.uiState.value.movies.map { it.title })
    }

    @Test
    fun movieClickEmitsNavigationEffect() = runTest(dispatcher) {
        val effect = async { viewModel.effect.first() }

        viewModel.onAction(HomeUiAction.MovieClicked("42"))

        assertEquals(HomeEffect.NavigateToDetail("42"), effect.await())
    }
}

private class InMemoryCategoryRepository(
    private val categories: List<Category>,
) : CategoryRepository {
    override suspend fun getCategories(): List<Category> = categories
    override suspend fun getCategory(categoryId: String): Category? = categories.find { it.id == categoryId }
    override suspend fun addCategory(draft: CategoryDraft): Category = error("Not needed in this test")
    override suspend fun updateCategory(category: Category): Category = error("Not needed in this test")
    override suspend fun deleteCategory(categoryId: String) = Unit
}

private class InMemoryMovieRepository(
    private val movies: MutableList<Movie>,
) : MovieRepository {
    override suspend fun getMovies(): List<Movie> = movies.toList()
    override suspend fun getMovie(movieId: String): Movie? = movies.find { it.id == movieId }
    override suspend fun addMovie(draft: MovieDraft): Movie = error("Not needed in this test")
    override suspend fun updateMovie(movie: Movie): Movie = movie
    override suspend fun deleteMovie(movieId: String) {
        movies.removeAll { it.id == movieId }
    }
    override suspend fun searchMovies(query: String): List<Movie> = movies.filter {
        it.title.contains(query, ignoreCase = true)
    }
    override suspend fun getMoviesByCategory(categoryId: String): List<Movie> = movies.filter {
        it.categoryId == categoryId
    }
    override suspend fun getMoviesSorted(sortBy: SortBy, ascending: Boolean): List<Movie> = movies.toList()
    override suspend fun exportBackup(): String = "{}"
    override suspend fun importBackup(jsonString: String) = Unit
}

private fun movie(
    id: String,
    title: String,
    updatedAt: Long,
): Movie {
    return Movie(
        id = id,
        title = title,
        description = "Description",
        releaseYear = 2024,
        categoryId = "science-fiction",
        posterUrl = null,
        runtimeMinutes = 120,
        isWatched = false,
        comment = null,
        userRating = null,
        imdbRating = null,
        externalIds = null,
        watchProviders = null,
        updatedAtEpochMillis = updatedAt,
    )
}
