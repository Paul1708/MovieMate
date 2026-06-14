package de.fh.erfurt.moviemate.movies.presentation.backup

data class BackupUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

sealed interface BackupEvent {
    data object ExportRequested : BackupEvent
    data class ImportRequested(val jsonString: String) : BackupEvent
}

sealed interface BackupEffect {
    data class ShareJson(val jsonString: String) : BackupEffect
    data class ShowMessage(val message: String) : BackupEffect
}