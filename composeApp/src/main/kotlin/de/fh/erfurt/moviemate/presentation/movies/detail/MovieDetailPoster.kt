package de.fh.erfurt.moviemate.presentation.movies.detail

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.movies.poster.decodePosterDataUri
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
internal fun MovieDetailPosterStage(
    posterUrl: String?,
    title: String,
    modifier: Modifier = Modifier,
) {
    val bitmap = rememberPosterBitmap(posterUrl)
    val stageShape = RoundedCornerShape(MovieMateShapes.cardRadius)
    val posterShape = RoundedCornerShape(MovieMateShapes.posterRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (posterUrl.isNullOrBlank()) 148.dp else 244.dp)
            .clip(stageShape)
            .background(
                Brush.linearGradient(
                    listOf(MovieMateColors.ScreenBackgroundGlow, MovieMateColors.AccentContainer)
                )
            )
            .border(MovieMateSpacing.dividerThickness, MovieMateColors.CardBorder, stageShape)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.55f)
                    .blur(24.dp),
            )
        }

        Box(
            modifier = Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    listOf(
                        MovieMateColors.ScreenBackground.copy(alpha = 0.22f),
                        MovieMateColors.ScreenBackground.copy(alpha = 0.82f),
                    )
                )
            )
        )

        BoxWithConstraints(
            modifier = Modifier.matchParentSize().padding(MovieMateSpacing.detailMediumSpacing),
            contentAlignment = Alignment.Center,
        ) {
            if (bitmap == null) {
                MovieMateIcon(
                    icon = MovieMateIcons.image,
                    tint = MovieMateColors.SecondaryText,
                    contentDescription = "No poster available",
                    modifier = Modifier.size(MovieMateSpacing.posterActionIconSize),
                )
            } else {
                val aspectRatio = bitmap.width.toFloat() / bitmap.height
                val width = minOf(maxWidth, maxHeight * aspectRatio)
                Image(
                    bitmap = bitmap,
                    contentDescription = "Poster for $title",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(width, width / aspectRatio)
                        .shadow(12.dp, posterShape, clip = false)
                        .clip(posterShape)
                        .background(Color.Black),
                )
            }
        }
    }
}

/**
 * Poster surface for detail and form screens. Local content URIs are rendered when available.
 */
@Composable
fun MovieDetailPoster(
    posterUrl: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showPlaceholderIcon: Boolean = false,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val description = if (posterUrl.isNullOrBlank()) {
        "Poster placeholder"
    } else {
        "Poster"
    }
    val bitmap = rememberPosterBitmap(posterUrl)
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
        when {
            bitmap != null -> {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            }

            showPlaceholderIcon -> {
                MovieMateIcon(
                    icon = MovieMateIcons.image,
                    tint = MovieMateColors.PrimaryText,
                    modifier = Modifier.size(MovieMateSpacing.posterPlaceholderIconSize)
                )
            }
        }
    }
}

@Composable
private fun rememberPosterBitmap(posterUrl: String?): ImageBitmap? {
    val context = LocalContext.current
    var posterBitmap by remember(context, posterUrl) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(context, posterUrl) {
        if (posterUrl.isNullOrBlank()) return@LaunchedEffect
        posterBitmap = withContext(Dispatchers.IO) {
            runCatching {
                when {
                    posterUrl.startsWith("data:image/") -> {
                        decodePosterDataUri(posterUrl)?.let { bytes ->
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                        }
                    }

                    posterUrl.startsWith("http") -> {
                        val connection = URL(posterUrl).openConnection().apply {
                            connectTimeout = 5_000
                            readTimeout = 5_000
                        }
                        connection.getInputStream().use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
                    }

                    else -> {
                        context.contentResolver.openInputStream(Uri.parse(posterUrl))?.use {
                            BitmapFactory.decodeStream(it)?.asImageBitmap()
                        }
                    }
                }
            }.getOrNull()
        }
    }

    return posterBitmap
}
