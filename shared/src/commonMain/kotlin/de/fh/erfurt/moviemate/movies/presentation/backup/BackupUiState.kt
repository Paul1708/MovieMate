package de.fh.erfurt.moviemate.movies.presentation.backup

/** Connection state shared by the Android and iOS backup-and-sync screens. */
enum class BackupAccountState {
    SignedOut,
    SigningIn,
    SignedIn,
}

/** Render state for backup, Google Drive authorization, and synchronization. */
data class BackupUiState(
    val accountState: BackupAccountState = BackupAccountState.SignedOut,
    val accountId: String? = null,
    val accountDisplayName: String? = null,
    val accountEmail: String? = null,
    val isSyncing: Boolean = false,
    val isSigningOut: Boolean = false,
    val lastSyncEpochMillis: Long? = null,
    val syncErrorMessage: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
) {
    val isBusy: Boolean
        get() = accountState == BackupAccountState.SigningIn ||
            isSyncing ||
            isSigningOut ||
            isLoading
}
