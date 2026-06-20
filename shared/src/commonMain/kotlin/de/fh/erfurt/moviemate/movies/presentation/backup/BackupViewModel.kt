package de.fh.erfurt.moviemate.movies.presentation.backup

import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ExportBackupUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ImportBackupUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BackupViewModel(
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<BackupEffect>()
    val effect: SharedFlow<BackupEffect> = _effect.asSharedFlow()

    fun onEvent(event: BackupEvent) {
        when (event) {
            is BackupEvent.ExportRequested -> exportData()
            is BackupEvent.ImportRequested -> importData(event.jsonString)
        }
    }

    private fun exportData() {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
            try {
                val jsonBackup = exportBackupUseCase()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                _effect.emit(BackupEffect.ShareJson(jsonBackup))
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                _effect.emit(BackupEffect.ShowMessage(e.message ?: "Export fehlgeschlagen."))
            }
        }
    }

    private fun importData(jsonString: String) {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
            try {
                importBackupUseCase(jsonString)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                _effect.emit(BackupEffect.ShowMessage("Import erfolgreich abgeschlossen."))
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                _effect.emit(BackupEffect.ShowMessage(e.message ?: "Import fehlgeschlagen."))
            }
        }
    }
}