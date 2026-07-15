package de.fh.erfurt.moviemate.presentation.movies.backup

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ExportBackupUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ImportBackupUseCase
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupAccountState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BackupScreenViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var repository: BackupMovieRepository
    private lateinit var coordinator: FakeBackupSyncCoordinator
    private lateinit var viewModel: BackupScreenViewModel

    @BeforeTest
    fun setUp() {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        repository = BackupMovieRepository()
        coordinator = FakeBackupSyncCoordinator()
        viewModel = BackupScreenViewModel(
            exportBackupUseCase = ExportBackupUseCase(repository),
            importBackupUseCase = ImportBackupUseCase(repository),
            syncCoordinator = coordinator,
            ioDispatcher = dispatcher,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun signInUpdatesStateAndEmitsAuthorizationEffect() = runTest(dispatcher) {
        val effect = async { viewModel.effect.first() }

        viewModel.onAction(BackupAction.SignInClicked)

        assertEquals(BackupAccountState.SigningIn, viewModel.uiState.value.accountState)
        assertEquals(BackupScreenEffect.AuthorizeGoogleDrive, effect.await())
    }

    @Test
    fun authorizationResultIsMirroredAndSyncCanBeRequested() = runTest(dispatcher) {
        val account = GoogleDriveAuthorizedAccount(
            accessToken = "token",
            accountId = "google-subject",
            accountName = "movie@example.com",
            displayName = "Movie Fan",
            email = "movie@example.com",
        )

        viewModel.onAction(BackupAction.AuthorizationSucceeded(account))

        assertEquals(BackupAccountState.SignedIn, viewModel.uiState.value.accountState)
        assertEquals("google-subject", viewModel.uiState.value.accountId)
        assertEquals("Movie Fan", viewModel.uiState.value.accountDisplayName)

        viewModel.onAction(BackupAction.SyncClicked)

        assertEquals(1, coordinator.syncRequests)
    }

    @Test
    fun signOutSetsBusyStateAndEmitsRevokeEffect() = runTest(dispatcher) {
        coordinator.authorizationSucceeded(
            GoogleDriveAuthorizedAccount(
                accessToken = "token",
                accountId = "google-subject",
                accountName = "movie@example.com",
                displayName = null,
                email = "movie@example.com",
            )
        )
        val effect = async { viewModel.effect.first() }

        viewModel.onAction(BackupAction.SignOutClicked)

        assertTrue(viewModel.uiState.value.isSigningOut)
        assertEquals(
            BackupScreenEffect.RevokeGoogleDriveAccess("movie@example.com"),
            effect.await(),
        )
    }

    @Test
    fun cancelledExportLeavesScreenUsable() = runTest(dispatcher) {
        repository.exportedJson = "{\"movies\":[],\"categories\":[]}"
        val effect = async { viewModel.effect.first() }

        viewModel.onAction(BackupAction.ExportClicked)

        assertIs<BackupScreenEffect.CreateExportDocument>(effect.await())
        assertTrue(viewModel.uiState.value.isLoading)

        viewModel.onAction(BackupAction.ExportCancelled)

        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun importedContentProducesSuccessState() = runTest(dispatcher) {
        val json = "{\"movies\":[],\"categories\":[]}"

        viewModel.onAction(BackupAction.ImportContentLoaded(json))

        assertEquals(json, repository.importedJson)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isSuccess)
    }
}

private class FakeBackupSyncCoordinator : BackupSyncCoordinator {
    private val _state = MutableStateFlow(GoogleDriveSyncState())
    override val state: StateFlow<GoogleDriveSyncState> = _state.asStateFlow()
    var syncRequests: Int = 0

    override fun storedSession(): GoogleDriveStoredSession? = null

    override fun authorizationStarted() {
        _state.value = _state.value.copy(accountState = BackupAccountState.SigningIn)
    }

    override fun authorizationSucceeded(account: GoogleDriveAuthorizedAccount) {
        _state.value = GoogleDriveSyncState(
            accountState = BackupAccountState.SignedIn,
            accountId = account.accountId,
            accountName = account.accountName,
            accountDisplayName = account.displayName,
            accountEmail = account.email,
        )
    }

    override fun authorizationCancelled(preserveStoredSession: Boolean) {
        _state.value = GoogleDriveSyncState()
    }

    override fun authorizationFailed(message: String, preserveStoredSession: Boolean) {
        _state.value = GoogleDriveSyncState(errorMessage = message)
    }

    override fun syncNow() {
        syncRequests += 1
    }

    override fun signOutStarted() {
        _state.value = _state.value.copy(isSigningOut = true)
    }

    override fun signOutCompleted() {
        _state.value = GoogleDriveSyncState()
    }

    override fun signOutFailed(message: String) {
        _state.value = _state.value.copy(isSigningOut = false, errorMessage = message)
    }
}

private class BackupMovieRepository : MovieRepository {
    var exportedJson: String = "{}"
    var importedJson: String? = null

    override suspend fun getMovies(): List<Movie> = emptyList()
    override suspend fun getMovie(movieId: String): Movie? = null
    override suspend fun addMovie(draft: MovieDraft): Movie = error("Not needed in this test")
    override suspend fun updateMovie(movie: Movie): Movie = error("Not needed in this test")
    override suspend fun deleteMovie(movieId: String) = Unit
    override suspend fun searchMovies(query: String): List<Movie> = emptyList()
    override suspend fun getMoviesByCategory(categoryId: String): List<Movie> = emptyList()
    override suspend fun getMoviesSorted(sortBy: SortBy, ascending: Boolean): List<Movie> = emptyList()
    override suspend fun exportBackup(): String = exportedJson
    override suspend fun importBackup(jsonString: String) {
        importedJson = jsonString
    }
}
