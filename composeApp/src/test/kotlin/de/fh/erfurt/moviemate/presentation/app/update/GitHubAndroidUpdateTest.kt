package de.fh.erfurt.moviemate.presentation.app.update

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GitHubAndroidUpdateTest {
    @Test
    fun usesAndroidVersionCodeAndBuildsTheDownloadLink() {
        val update = parseAndroidUpdate(release(), installedVersionCode = 9)

        assertEquals(
            AndroidUpdate(
                versionName = "1.2.0",
                versionCode = 10,
                downloadUrl = "https://github.com/Paul1708/MovieMate/releases/download/v1.2.0/MovieMate-android-10.apk",
            ),
            update,
        )
        assertNull(parseAndroidUpdate(release(), installedVersionCode = 10))
        assertNull(parseAndroidUpdate(release(), installedVersionCode = 11))
    }

    @Test
    fun ignoresMissingOrUnuploadedApks() {
        assertNull(parseAndroidUpdate(release(name = "source.zip"), installedVersionCode = 1))
        assertNull(parseAndroidUpdate(release(state = "starter"), installedVersionCode = 1))
    }

    @Test
    fun ignoresInvalidReleaseMetadata() {
        assertNull(parseAndroidUpdate("{", installedVersionCode = 1))
        assertNull(parseAndroidUpdate(release(tag = "v1.2.0-beta"), installedVersionCode = 1))
        assertNull(
            parseAndroidUpdate(release(name = "MovieMate-android-9999999999.apk"), installedVersionCode = 1)
        )
    }

    private fun release(
        tag: String = "v1.2.0",
        name: String = "MovieMate-android-10.apk",
        state: String = "uploaded",
    ): String = """
        {
          "tag_name": "$tag",
          "assets": [
            {"name": "notes.txt", "state": "uploaded"},
            {"name": "$name", "state": "$state", "browser_download_url": "https://example.com/fake.apk"}
          ]
        }
    """.trimIndent()
}
