package de.fh.erfurt.moviemate.movies.presentation.backup

/** Render state for the Android backup screen. */
data class BackupUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
)
