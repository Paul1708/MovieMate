package de.fh.erfurt.moviemate.presentation.movies.add

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieMetadataRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.AddMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.FetchMovieDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AddMovieViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var metadataRepository: StubMovieMetadataRepository
    private lateinit var viewModel: AddMovieViewModel

    @BeforeTest
    fun setUp() {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        metadataRepository = StubMovieMetadataRepository()
        viewModel = AddMovieViewModel(
            getCategoriesUseCase = GetCategoriesUseCase(EmptyCategoryRepository()),
            addMovieUseCase = AddMovieUseCase(EmptyMovieRepository()),
            fetchMovieDataUseCase = FetchMovieDataUseCase(metadataRepository),
            ioDispatcher = dispatcher,
        )
        viewModel.onAction(AddMovieAction.LoadRequested)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchMovieDataPopulatesFormFields() = runTest(dispatcher) {
        metadataRepository.result = FetchedMovieData(
            title = "Alien",
            description = "A space horror classic.",
            releaseYear = 1979,
            posterUrl = "https://example.com/alien.jpg",
            runtimeMinutes = 117,
            imdbRating = 8.5,
        )

        viewModel.onAction(AddMovieAction.TitleChanged("  Alien  "))
        viewModel.onAction(AddMovieAction.FetchMovieDataClicked)

        val state = viewModel.uiState.value
        assertEquals("Alien", metadataRepository.requestedTitle)
        assertEquals("Alien", state.title)
        assertEquals("A space horror classic.", state.description)
        assertEquals("1979", state.releaseYear)
        assertEquals("https://example.com/alien.jpg", state.posterUrl)
        assertEquals("117", state.runtimeMinutes)
        assertEquals("8.5", state.imdbRating)
        assertFalse(state.isFetching)
        assertNull(state.errorMessage)
    }

    @Test
    fun fetchWithoutTitleShowsValidationError() = runTest(dispatcher) {
        viewModel.onAction(AddMovieAction.FetchMovieDataClicked)

        assertNull(metadataRepository.requestedTitle)
        assertEquals("Please enter a movie title first.", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isFetching)
    }
}

private class StubMovieMetadataRepository : MovieMetadataRepository {
    var result: FetchedMovieData? = null
    var requestedTitle: String? = null

    override suspend fun fetchMovieDataByTitle(title: String): FetchedMovieData? {
        requestedTitle = title
        return result
    }
}

private class EmptyCategoryRepository : CategoryRepository {
    override suspend fun getCategories(): List<Category> = emptyList()
    override suspend fun getCategory(categoryId: String): Category? = null
    override suspend fun addCategory(draft: CategoryDraft): Category = error("Not needed in this test")
    override suspend fun updateCategory(category: Category): Category = error("Not needed in this test")
    override suspend fun deleteCategory(categoryId: String) = Unit
}

private class EmptyMovieRepository : MovieRepository {
    override suspend fun getMovies(): List<Movie> = emptyList()
    override suspend fun getMovie(movieId: String): Movie? = null
    override suspend fun addMovie(draft: MovieDraft): Movie = error("Not needed in this test")
    override suspend fun updateMovie(movie: Movie): Movie = error("Not needed in this test")
    override suspend fun deleteMovie(movieId: String) = Unit
    override suspend fun searchMovies(query: String): List<Movie> = emptyList()
    override suspend fun getMoviesByCategory(categoryId: String): List<Movie> = emptyList()
    override suspend fun getMoviesSorted(sortBy: SortBy, ascending: Boolean): List<Movie> = emptyList()
    override suspend fun exportBackup(): String = "{}"
    override suspend fun importBackup(jsonString: String) = Unit
}
