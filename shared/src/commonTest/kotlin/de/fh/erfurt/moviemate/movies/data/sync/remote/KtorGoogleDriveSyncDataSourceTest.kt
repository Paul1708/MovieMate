package de.fh.erfurt.moviemate.movies.data.sync.remote

import de.fh.erfurt.moviemate.movies.data.remote.client.MoviesHttpClientFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class KtorGoogleDriveSyncDataSourceTest {
    @Test
    fun `getCurrentAccountId reads canonical Drive permission id`() = runTest {
        var requestData: HttpRequestData? = null
        val engine = MockEngine { request ->
            requestData = request
            respondJson("""{"user":{"permissionId":"drive-user-123"}}""")
        }
        val client = MoviesHttpClientFactory().create(engine)

        val accountId = KtorGoogleDriveSyncDataSource(client).getCurrentAccountId("token")

        assertEquals("drive-user-123", accountId)
        assertEquals("/drive/v3/about", requestData?.url?.encodedPath)
        assertEquals("user(permissionId)", requestData?.url?.parameters?.get("fields"))
        assertEquals("Bearer token", requestData?.headers?.get(HttpHeaders.Authorization))
    }

    @Test
    fun `listSnapshots follows pagination and downloads matching app-data files`() = runTest {
        val requests = mutableListOf<HttpRequestData>()
        val engine = MockEngine { request ->
            requests += request
            when (requests.size) {
                1 -> respondJson(
                    """
                    {
                      "nextPageToken": "page-2",
                      "files": [
                        {"id":"file-1","name":"moviemate-sync-v1-device-a.json","version":"4"},
                        {"id":"ignored","name":"unrelated.json","version":"1"}
                      ]
                    }
                    """.trimIndent(),
                )
                2 -> respondJson(
                    """
                    {
                      "files": [
                        {"id":"file-2","name":"moviemate-sync-v1-device-b.json","version":"5"}
                      ]
                    }
                    """.trimIndent(),
                )
                3 -> respondJson("{\"deviceId\":\"device-a\"}")
                4 -> respondJson("{\"deviceId\":\"device-b\"}")
                else -> error("Unexpected request: ${request.url}")
            }
        }
        val client = MoviesHttpClientFactory().create(engine)
        val dataSource = KtorGoogleDriveSyncDataSource(client)

        val files = dataSource.listSnapshots("token")

        assertEquals(listOf("file-1", "file-2"), files.map(RemoteSyncSnapshotFile::id))
        assertEquals(listOf(4L, 5L), files.map(RemoteSyncSnapshotFile::version))
        assertEquals("100", requests[0].url.parameters["pageSize"])
        assertNull(requests[0].url.parameters["pageToken"])
        assertEquals("page-2", requests[1].url.parameters["pageToken"])
        requests.forEach { request ->
            assertEquals("Bearer token", request.headers[HttpHeaders.Authorization])
        }
    }
}

private fun io.ktor.client.engine.mock.MockRequestHandleScope.respondJson(
    content: String,
) = respond(
    content = content,
    status = HttpStatusCode.OK,
    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
)
