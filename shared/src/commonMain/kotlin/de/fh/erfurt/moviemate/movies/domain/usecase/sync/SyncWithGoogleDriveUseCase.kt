package de.fh.erfurt.moviemate.movies.domain.usecase.sync

import de.fh.erfurt.moviemate.movies.domain.sync.CloudSyncRepository
import de.fh.erfurt.moviemate.movies.domain.sync.SyncResult

class SyncWithGoogleDriveUseCase(
    private val cloudSyncRepository: CloudSyncRepository,
) {
    suspend operator fun invoke(
        accessToken: String,
        accountId: String,
    ): SyncResult {
        require(accessToken.isNotBlank()) { "Google Drive access token must not be blank." }
        require(accountId.isNotBlank()) { "Google account id must not be blank." }

        return cloudSyncRepository.sync(
            accessToken = accessToken,
            accountId = accountId,
        )
    }
}
