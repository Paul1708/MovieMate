package de.fh.erfurt.moviemate.presentation.movies.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ExportBackupUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ImportBackupUseCase
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupAccountState
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupUiState
import de.fh.erfurt.moviemate.presentation.app.throwIfCancellation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BackupScreenViewModel(
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase,
    private val syncCoordinator: BackupSyncCoordinator,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<BackupScreenEffect>()
    val effect: SharedFlow<BackupScreenEffect> = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            syncCoordinator.state.collect { syncState ->
                _uiState.update { state ->
                    state.copy(
                        accountState = syncState.accountState,
                        accountId = syncState.accountId,
                        accountDisplayName = syncState.accountDisplayName,
                        accountEmail = syncState.accountEmail,
                        isSyncing = syncState.isSyncing,
                        isSigningOut = syncState.isSigningOut,
                        lastSyncEpochMillis = syncState.lastSyncEpochMillis,
                        syncErrorMessage = syncState.errorMessage,
                    )
                }
            }
        }
    }

    fun onAction(action: BackupAction) {
        when (action) {
            BackupAction.SignInClicked -> signIn()
            is BackupAction.AuthorizationSucceeded -> syncCoordinator.authorizationSucceeded(action.account)
            BackupAction.AuthorizationCancelled -> syncCoordinator.authorizationCancelled()
            is BackupAction.AuthorizationFailed -> syncCoordinator.authorizationFailed(action.message)
            BackupAction.SyncClicked -> syncCoordinator.syncNow()
            BackupAction.SignOutClicked -> signOut()
            BackupAction.SignOutCompleted -> syncCoordinator.signOutCompleted()
            is BackupAction.SignOutFailed -> syncCoordinator.signOutFailed(action.message)
            BackupAction.ExportClicked -> exportBackup()
            BackupAction.ImportClicked -> {
                clearManualStatus()
                emitEffect(BackupScreenEffect.OpenImportDocument)
            }

            is BackupAction.ImportContentLoaded -> importBackup(action.json)
            BackupAction.ExportSaved -> _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = null,
                    isSuccess = true,
                )
            }

            BackupAction.ExportCancelled -> _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = null,
                    isSuccess = false,
                )
            }

            is BackupAction.PlatformFailure -> _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = action.message,
                    isSuccess = false,
                )
            }
        }
    }

    private fun signIn() {
        if (_uiState.value.accountState != BackupAccountState.SignedOut) return
        syncCoordinator.authorizationStarted()
        emitEffect(BackupScreenEffect.AuthorizeGoogleDrive)
    }

    private fun signOut() {
        if (_uiState.value.accountState != BackupAccountState.SignedIn || _uiState.value.isSigningOut) return
        syncCoordinator.signOutStarted()
        emitEffect(
            BackupScreenEffect.RevokeGoogleDriveAccess(
                accountName = syncCoordinator.state.value.accountName,
            )
        )
    }

    private fun exportBackup() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false,
                )
            }
            runCatching {
                withContext(ioDispatcher) { exportBackupUseCase() }
            }.onSuccess { json ->
                _effect.emit(BackupScreenEffect.CreateExportDocument(json))
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.backupMessage("Backup could not be exported."),
                        isSuccess = false,
                    )
                }
            }
        }
    }

    private fun importBackup(json: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false,
                )
            }
            runCatching {
                withContext(ioDispatcher) { importBackupUseCase(json) }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        isSuccess = true,
                    )
                }
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.backupMessage("Backup file could not be imported."),
                        isSuccess = false,
                    )
                }
            }
        }
    }

    private fun clearManualStatus() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                isSuccess = false,
            )
        }
    }

    private fun emitEffect(effect: BackupScreenEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}

sealed interface BackupAction {
    data object SignInClicked : BackupAction
    data class AuthorizationSucceeded(val account: GoogleDriveAuthorizedAccount) : BackupAction
    data object AuthorizationCancelled : BackupAction
    data class AuthorizationFailed(val message: String) : BackupAction
    data object SyncClicked : BackupAction
    data object SignOutClicked : BackupAction
    data object SignOutCompleted : BackupAction
    data class SignOutFailed(val message: String) : BackupAction
    data object ExportClicked : BackupAction
    data object ImportClicked : BackupAction
    data class ImportContentLoaded(val json: String) : BackupAction
    data object ExportSaved : BackupAction
    data object ExportCancelled : BackupAction
    data class PlatformFailure(val message: String) : BackupAction
}

sealed interface BackupScreenEffect {
    data object AuthorizeGoogleDrive : BackupScreenEffect
    data class RevokeGoogleDriveAccess(val accountName: String?) : BackupScreenEffect
    data class CreateExportDocument(val json: String) : BackupScreenEffect
    data object OpenImportDocument : BackupScreenEffect
}

private fun Throwable.backupMessage(fallback: String): String {
    return message?.takeIf(String::isNotBlank) ?: fallback
}
