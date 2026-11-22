package de.fh.erfurt.moviemate.presentation.app.update

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI

private const val Repository = "Paul1708/MovieMate"
private val ReleaseTag = Regex("v[0-9]+\\.[0-9]+\\.[0-9]+")
private val ApkName = Regex("MovieMate-android-([1-9][0-9]*)\\.apk")

internal data class AndroidUpdate(
    val versionName: String,
    val versionCode: Int,
    val downloadUrl: String,
)

internal suspend fun findAndroidUpdate(installedVersionCode: Int): AndroidUpdate? =
    withContext(Dispatchers.IO) {
        val connection = URI("https://api.github.com/repos/$Repository/releases/latest")
            .toURL().openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return@withContext null

            connection.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                parseAndroidUpdate(reader.readText(), installedVersionCode)
            }
        } catch (_: IOException) {
            null
        } finally {
            connection.disconnect()
        }
    }

internal fun parseAndroidUpdate(response: String, installedVersionCode: Int): AndroidUpdate? {
    val release = try {
        Json.parseToJsonElement(response) as? JsonObject ?: return null
    } catch (_: IllegalArgumentException) {
        return null
    }
    val tag = release.string("tag_name")?.takeIf(ReleaseTag::matches) ?: return null
    val assets = release["assets"] as? JsonArray ?: return null
    return assets.mapNotNull { asset ->
        val file = (asset as? JsonObject)?.takeIf { it.string("state") == "uploaded" }
            ?: return@mapNotNull null
        val name = file.string("name") ?: return@mapNotNull null
        val versionCode = ApkName.matchEntire(name)?.groupValues?.get(1)?.toIntOrNull()
            ?: return@mapNotNull null
        AndroidUpdate(
            versionName = tag.removePrefix("v"),
            versionCode = versionCode,
            downloadUrl = "https://github.com/$Repository/releases/download/$tag/$name",
        )
    }.singleOrNull()?.takeIf { it.versionCode > installedVersionCode }
}

private fun JsonObject.string(key: String): String? =
    (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content
