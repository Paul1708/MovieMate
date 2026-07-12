package de.fh.erfurt.moviemate.presentation.movies.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ExportBackupUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ImportBackupUseCase
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
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<BackupScreenEffect>()
    val effect: SharedFlow<BackupScreenEffect> = _effect.asSharedFlow()

    fun onAction(action: BackupAction) {
        when (action) {
            BackupAction.ExportClicked -> exportBackup()
            BackupAction.ImportClicked -> emitEffect(BackupScreenEffect.OpenImportDocument)
            is BackupAction.ImportContentLoaded -> importBackup(action.json)
            BackupAction.ExportSaved -> _uiState.value = BackupUiState(isSuccess = true)
            is BackupAction.PlatformFailure -> {
                _uiState.value = BackupUiState(errorMessage = action.message)
            }
        }
    }

    private fun exportBackup() {
        viewModelScope.launch {
            _uiState.value = BackupUiState(isLoading = true)
            runCatching {
                withContext(ioDispatcher) { exportBackupUseCase() }
            }.onSuccess { json ->
                _effect.emit(BackupScreenEffect.CreateExportDocument(json))
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.value = BackupUiState(
                    errorMessage = throwable.backupMessage("Backup could not be exported.")
                )
            }
        }
    }

    private fun importBackup(json: String) {
        viewModelScope.launch {
            _uiState.value = BackupUiState(isLoading = true)
            runCatching {
                withContext(ioDispatcher) { importBackupUseCase(json) }
            }.onSuccess {
                _uiState.value = BackupUiState(isSuccess = true)
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.value = BackupUiState(
                    errorMessage = throwable.backupMessage("Backup file could not be imported.")
                )
            }
        }
    }

    private fun emitEffect(effect: BackupScreenEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}

sealed interface BackupAction {
    data object ExportClicked : BackupAction
    data object ImportClicked : BackupAction
    data class ImportContentLoaded(val json: String) : BackupAction
    data object ExportSaved : BackupAction
    data class PlatformFailure(val message: String) : BackupAction
}

sealed interface BackupScreenEffect {
    data class CreateExportDocument(val json: String) : BackupScreenEffect
    data object OpenImportDocument : BackupScreenEffect
}

private fun Throwable.backupMessage(fallback: String): String {
    return message?.takeIf(String::isNotBlank) ?: fallback
}
