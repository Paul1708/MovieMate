package de.fh.erfurt.moviemate.presentation.movies.add

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieSearchMatch
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieSearchResults
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieMetadataRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.movies.domain.usecase.category.AddCategoryUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddMovieViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var categoryRepository: InMemoryCategoryRepository
    private lateinit var metadataRepository: StubMovieMetadataRepository
    private lateinit var viewModel: AddMovieViewModel

    @BeforeTest
    fun setUp() {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        categoryRepository = InMemoryCategoryRepository()
        metadataRepository = StubMovieMetadataRepository()
        viewModel = AddMovieViewModel(
            getCategoriesUseCase = GetCategoriesUseCase(categoryRepository),
            addMovieUseCase = AddMovieUseCase(EmptyMovieRepository()),
            addCategoryUseCase = AddCategoryUseCase(categoryRepository),
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
        viewModel.onAction(AddMovieAction.ReleaseYearChanged("1979"))
        viewModel.onAction(AddMovieAction.FetchMovieDataClicked)

        val state = viewModel.uiState.value
        assertEquals("Alien", metadataRepository.requestedTitle)
        assertEquals("1979", metadataRepository.requestedYear)
        assertEquals("tt0078748", metadataRepository.requestedImdbId)
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
        assertEquals("Please enter a movie title first.", viewModel.uiState.value.lookup.error)
        assertFalse(viewModel.uiState.value.isFetching)
    }

    @Test
    fun multipleMatchesWaitForSelectionAndKeepPersonalFields() = runTest(dispatcher) {
        metadataRepository.matches = listOf(
            MovieSearchMatch("tt0087182", "Dune", "1984", null),
            MovieSearchMatch("tt1160419", "Dune", "2021", null),
        )
        metadataRepository.result = fetchedMovieData(genres = emptyList()).copy(title = "Dune", releaseYear = 2021)

        viewModel.onAction(AddMovieAction.TitleChanged("Dune"))
        viewModel.onAction(AddMovieAction.UserRatingChanged("9"))
        viewModel.onAction(AddMovieAction.CommentChanged("My note"))
        viewModel.onAction(AddMovieAction.WatchedChanged(true))
        viewModel.onAction(AddMovieAction.FetchMovieDataClicked)

        assertEquals(metadataRepository.matches, viewModel.uiState.value.lookup.results?.matches)
        assertNull(metadataRepository.requestedImdbId)
        assertEquals("", viewModel.uiState.value.description)

        viewModel.onAction(AddMovieAction.MovieMatchSelected("tt1160419"))

        val state = viewModel.uiState.value
        assertEquals("tt1160419", metadataRepository.requestedImdbId)
        assertEquals("2021", state.releaseYear)
        assertEquals("9", state.userRating)
        assertEquals("My note", state.comment)
        assertTrue(state.isWatched)
        assertNull(state.lookup.results)
    }

    @Test
    fun fetchSelectsAnExistingCategoryFromAnyFetchedGenre() = runTest(dispatcher) {
        categoryRepository.seed(
            Category(
                id = "science-fiction",
                name = "Science Fiction",
                iconKey = "sci_fi",
                updatedAtEpochMillis = 1,
            )
        )
        viewModel.onAction(AddMovieAction.LoadRequested)
        metadataRepository.result = fetchedMovieData(genres = listOf("Horror", "Sci-Fi"))

        viewModel.onAction(AddMovieAction.TitleChanged("Alien"))
        viewModel.onAction(AddMovieAction.FetchMovieDataClicked)

        assertEquals("science-fiction", viewModel.uiState.value.categoryId)
        assertFalse(viewModel.uiState.value.isAddCategoryDialogVisible)
    }

    @Test
    fun fetchOffersMissingGenreAndSelectsCreatedCategory() = runTest(dispatcher) {
        metadataRepository.result = fetchedMovieData(genres = listOf("Horror", "Sci-Fi"))

        viewModel.onAction(AddMovieAction.TitleChanged("Alien"))
        viewModel.onAction(AddMovieAction.FetchMovieDataClicked)

        assertTrue(viewModel.uiState.value.isAddCategoryDialogVisible)
        assertEquals("Horror", viewModel.uiState.value.addCategoryName)
        assertEquals("horror", viewModel.uiState.value.addCategoryIconKey)

        viewModel.onAction(AddMovieAction.AddCategoryIconSelected("drama"))
        viewModel.onAction(AddMovieAction.AddCategoryConfirmed)

        val state = viewModel.uiState.value
        val createdCategory = state.availableCategories.single()
        assertEquals(createdCategory.id, state.categoryId)
        assertEquals("Horror", createdCategory.name)
        assertEquals("drama", createdCategory.iconKey)
        assertFalse(state.isAddCategoryDialogVisible)
        assertFalse(state.isAddingCategory)
    }
}

private class StubMovieMetadataRepository : MovieMetadataRepository {
    var result: FetchedMovieData? = null
    var requestedTitle: String? = null
    var requestedYear: String? = null
    var requestedImdbId: String? = null
    var matches = listOf(MovieSearchMatch("tt0078748", "Alien", "1979", null))

    override suspend fun searchMovies(title: String, year: String?, mediaType: MediaType): MovieSearchResults {
        requestedTitle = title
        requestedYear = year
        return MovieSearchResults(matches)
    }

    override suspend fun fetchMovieDataByImdbId(imdbId: String, mediaType: MediaType): FetchedMovieData? {
        requestedImdbId = imdbId
        return result
    }
}

private class InMemoryCategoryRepository : CategoryRepository {
    private val categories = mutableListOf<Category>()

    fun seed(category: Category) {
        categories += category
    }

    override suspend fun getCategories(): List<Category> = categories.toList()
    override suspend fun getCategory(categoryId: String): Category? = categories.find { it.id == categoryId }
    override suspend fun addCategory(draft: CategoryDraft): Category {
        return Category(
            id = "category-${categories.size + 1}",
            name = draft.name,
            iconKey = draft.iconKey,
            updatedAtEpochMillis = draft.updatedAtEpochMillis,
        ).also(categories::add)
    }
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

private fun fetchedMovieData(genres: List<String>): FetchedMovieData {
    return FetchedMovieData(
        title = "Alien",
        description = "A space horror classic.",
        releaseYear = 1979,
        posterUrl = "https://example.com/alien.jpg",
        runtimeMinutes = 117,
        imdbRating = 8.5,
        genres = genres,
    )
}
