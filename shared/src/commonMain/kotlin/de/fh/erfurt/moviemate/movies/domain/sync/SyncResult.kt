package de.fh.erfurt.moviemate.movies.domain.sync

data class SyncResult(
    val movieCount: Int,
    val categoryCount: Int,
    val remoteSnapshotCount: Int,
    val localDataChanged: Boolean,
)

class SyncAccountMismatchException : IllegalStateException(
    "Local MovieMate data is linked to another Google account. " +
        "Sign in with the previously linked account before syncing.",
)

class CloudSyncAuthenticationException : IllegalStateException(
    "Google Drive authorization has expired or does not include app-data access.",
)

class CloudSyncException(message: String, cause: Throwable? = null) :
    IllegalStateException(message, cause)
