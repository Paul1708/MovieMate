package de.fh.erfurt.moviemate.presentation.movies.detail

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Poster surface for detail and form screens. Local content URIs are rendered when available.
 */
@Composable
fun MovieDetailPoster(
    posterUrl: String?,
    onClick: (() -> Unit)? = null,
    showPlaceholderIcon: Boolean = false,
    modifier: Modifier = Modifier
) {
    val description = if (posterUrl.isNullOrBlank()) {
        "Movie poster placeholder"
    } else {
        "Movie poster"
    }
    val context = LocalContext.current
    var posterBitmap by remember(posterUrl) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(context, posterUrl) {
        posterBitmap = posterUrl
            ?.takeIf { url -> url.isNotBlank() }
            ?.let { url ->
                withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openInputStream(Uri.parse(url))?.use { stream ->
                            BitmapFactory.decodeStream(stream)?.asImageBitmap()
                        }
                    }.getOrNull()
                }
            }
    }

    val shape = RoundedCornerShape(MovieMateShapes.cardRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MovieMateColors.ScreenBackgroundGlow,
                        MovieMateColors.PosterPlaceholder,
                        MovieMateColors.AccentContainer
                    )
                )
            )
            .then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        val bitmap = posterBitmap
        when {
            bitmap != null -> {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            showPlaceholderIcon -> {
                MovieMateIcon(
                    icon = MovieMateIcons.image,
                    tint = MovieMateColors.PrimaryText,
                    secondaryTint = MovieMateColors.AccentLight,
                    modifier = Modifier.size(MovieMateSpacing.posterPlaceholderIconSize)
                )
            }
        }
    }
}
