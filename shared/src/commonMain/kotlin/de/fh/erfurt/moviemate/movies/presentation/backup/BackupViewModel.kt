package de.fh.erfurt.moviemate.movies.presentation.backup

import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ExportBackupUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ImportBackupUseCase

class BackupViewModel(
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase
) {

    val initialState = BackupUiState()

    fun onEvent(event: BackupEvent) {
        when (event) {
            is BackupEvent.ExportRequested -> Unit
            is BackupEvent.ImportRequested -> Unit
        }
    }
}