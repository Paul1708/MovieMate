package de.fh.erfurt.moviemate.movies.data.sync.remote

import de.fh.erfurt.moviemate.movies.domain.sync.CloudSyncAuthenticationException
import de.fh.erfurt.moviemate.movies.domain.sync.CloudSyncException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class KtorGoogleDriveSyncDataSource(
    private val httpClient: HttpClient,
) : GoogleDriveSyncDataSource {
    override suspend fun getCurrentAccountId(accessToken: String): String {
        require(accessToken.isNotBlank()) { "Google Drive access token must not be blank." }

        val response = httpClient.get(DriveAboutUrl) {
            bearerAuth(accessToken)
            parameter("fields", "user(permissionId)")
        }
        response.requireSuccess("Google Drive account lookup")
        return response.body<DriveAboutDto>()
            .user
            .permissionId
            .takeIf(String::isNotBlank)
            ?: throw CloudSyncException("Google Drive did not return a stable account identifier.")
    }

    override suspend fun listSnapshots(accessToken: String): List<RemoteSyncSnapshotFile> {
        require(accessToken.isNotBlank()) { "Google Drive access token must not be blank." }

        val metadata = mutableListOf<DriveFileDto>()
        var pageToken: String? = null
        do {
            val currentPageToken = pageToken
            val response = httpClient.get(DriveFilesUrl) {
                bearerAuth(accessToken)
                parameter("spaces", AppDataFolder)
                parameter("q", "name contains '$SyncSnapshotFilePrefix' and trashed = false")
                parameter("pageSize", DriveListPageSize)
                parameter("fields", "nextPageToken,files(id,name,version)")
                if (currentPageToken != null) parameter("pageToken", currentPageToken)
            }
            response.requireSuccess("Google Drive snapshot listing")
            val page = response.body<DriveFileListDto>()
            metadata += page.files.filter { file ->
                file.name.startsWith(SyncSnapshotFilePrefix) &&
                    file.name.endsWith(SyncSnapshotFileSuffix)
            }
            pageToken = page.nextPageToken
        } while (pageToken != null)

        return metadata.map { file ->
            val response = httpClient.get("$DriveFilesUrl/${file.id}") {
                bearerAuth(accessToken)
                parameter("alt", "media")
            }
            response.requireSuccess("Google Drive snapshot download")
            RemoteSyncSnapshotFile(
                id = file.id,
                name = file.name,
                version = file.version?.toLongOrNull(),
                content = response.bodyAsText(),
            )
        }
    }

    override suspend fun writeSnapshot(
        accessToken: String,
        fileName: String,
        existingFileId: String?,
        content: String,
    ) {
        require(accessToken.isNotBlank()) { "Google Drive access token must not be blank." }
        require(fileName.startsWith(SyncSnapshotFilePrefix) && fileName.endsWith(SyncSnapshotFileSuffix)) {
            "Unexpected MovieMate sync snapshot file name."
        }

        val fileId = existingFileId ?: createSnapshotFile(accessToken, fileName)
        val uploadResponse = httpClient.patch("$DriveUploadFilesUrl/$fileId") {
            bearerAuth(accessToken)
            parameter("uploadType", "media")
            setBody(TextContent(content, ContentType.Application.Json))
        }
        uploadResponse.requireSuccess("Google Drive snapshot upload")
    }

    private suspend fun createSnapshotFile(
        accessToken: String,
        fileName: String,
    ): String {
        val response = httpClient.post(DriveFilesUrl) {
            bearerAuth(accessToken)
            contentType(ContentType.Application.Json)
            setBody(
                CreateDriveFileDto(
                    name = fileName,
                    parents = listOf(AppDataFolder),
                    mimeType = ContentType.Application.Json.toString(),
                ),
            )
            parameter("fields", "id,name,version")
        }
        response.requireSuccess("Google Drive snapshot creation")
        return response.body<DriveFileDto>().id
    }
}

private suspend fun HttpResponse.requireSuccess(operation: String) {
    when {
        status == HttpStatusCode.Unauthorized || status == HttpStatusCode.Forbidden -> {
            throw CloudSyncAuthenticationException()
        }
        status.value !in 200..299 -> {
            throw CloudSyncException("$operation failed with HTTP ${status.value}.")
        }
    }
}

@Serializable
private data class DriveFileListDto(
    val nextPageToken: String? = null,
    val files: List<DriveFileDto> = emptyList(),
)

@Serializable
private data class DriveFileDto(
    val id: String,
    val name: String,
    val version: String? = null,
)

@Serializable
private data class CreateDriveFileDto(
    val name: String,
    val parents: List<String>,
    @SerialName("mimeType") val mimeType: String,
)

@Serializable
private data class DriveAboutDto(
    val user: DriveUserDto,
)

@Serializable
private data class DriveUserDto(
    val permissionId: String,
)

private const val DriveFilesUrl = "https://www.googleapis.com/drive/v3/files"
private const val DriveUploadFilesUrl = "https://www.googleapis.com/upload/drive/v3/files"
private const val DriveAboutUrl = "https://www.googleapis.com/drive/v3/about"
private const val AppDataFolder = "appDataFolder"
private const val DriveListPageSize = 100
