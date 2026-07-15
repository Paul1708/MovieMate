package de.fh.erfurt.moviemate.presentation.movies.poster

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Copies a user-selected poster into the movie record as compressed image data.
 *
 * A persisted `content://` URI only works on the device that selected it. A data URI
 * remains readable after a backup restore and can therefore travel through cloud sync.
 */
suspend fun Context.posterDataUri(
    uri: Uri,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): String = withContext(ioDispatcher) {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    val boundsStream = contentResolver.openInputStream(uri)
        ?: error("Selected poster could not be opened.")
    boundsStream.use { stream ->
        BitmapFactory.decodeStream(stream, null, bounds)
    }

    require(bounds.outWidth > 0 && bounds.outHeight > 0) {
        "Selected poster is not a supported image."
    }

    val options = BitmapFactory.Options().apply {
        inSampleSize = calculateSampleSize(
            width = bounds.outWidth,
            height = bounds.outHeight,
            maxDimension = PosterMaxDimensionPixels,
        )
    }
    val decoded = contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, options)
    } ?: error("Selected poster could not be decoded.")

    val scaled = decoded.scaledToFit(PosterMaxDimensionPixels)
    val bytes = scaled.compressToPortableJpeg()

    if (scaled !== decoded) scaled.recycle()
    decoded.recycle()

    val encoded = Base64.encodeToString(bytes, Base64.NO_WRAP)
    "data:image/jpeg;base64,$encoded"
}

fun decodePosterDataUri(value: String): ByteArray? {
    if (!value.startsWith(PosterDataUriPrefix)) return null
    val encoded = value.substringAfter(',', missingDelimiterValue = "")
    if (encoded.isBlank()) return null
    return runCatching { Base64.decode(encoded, Base64.DEFAULT) }.getOrNull()
}

private fun calculateSampleSize(width: Int, height: Int, maxDimension: Int): Int {
    var sampleSize = 1
    while (width / sampleSize > maxDimension * 2 || height / sampleSize > maxDimension * 2) {
        sampleSize *= 2
    }
    return sampleSize
}

private fun Bitmap.scaledToFit(maxDimension: Int): Bitmap {
    val largestDimension = maxOf(width, height)
    if (largestDimension <= maxDimension) return this

    val scale = maxDimension.toFloat() / largestDimension.toFloat()
    return Bitmap.createScaledBitmap(
        this,
        (width * scale).toInt().coerceAtLeast(1),
        (height * scale).toInt().coerceAtLeast(1),
        true,
    )
}

private fun Bitmap.compressToPortableJpeg(): ByteArray {
    var quality = PosterJpegQuality
    while (quality >= PosterMinJpegQuality) {
        val bytes = ByteArrayOutputStream().use { output ->
            check(compress(Bitmap.CompressFormat.JPEG, quality, output)) {
                "Selected poster could not be compressed."
            }
            output.toByteArray()
        }
        if (bytes.size <= PosterMaxEncodedBytes) return bytes
        quality -= PosterJpegQualityStep
    }
    error("Selected poster is too large to synchronize.")
}

private const val PosterDataUriPrefix = "data:image/"
private const val PosterMaxDimensionPixels = 1_600
private const val PosterJpegQuality = 82
private const val PosterMinJpegQuality = 42
private const val PosterJpegQualityStep = 8
private const val PosterMaxEncodedBytes = 3 * 1024 * 1024
