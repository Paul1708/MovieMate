package de.fh.erfurt.moviemate.presentation.app.opening

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.fh.erfurt.moviemate.R
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenBackground
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing
@Preview
@Composable
fun MovieMateOpeningScreen(
    modifier: Modifier = Modifier,
    message: String = "App vorbereiten...",
    errorMessage: String? = null,
    onRetryClicked: (() -> Unit)? = null,
) {
    MovieMateScreenBackground(modifier = modifier) {
        OpeningFilmReels(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = BrandOffsetY),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OpeningLogo()

            Spacer(modifier = Modifier.height(LogoTitleSpacing))

            MovieMateWordmark()

            Spacer(modifier = Modifier.height(ClaimTopSpacing))

            Text(
                text = "Deine Filme. Deine Sammlung.",
                style = MaterialTheme.typography.bodyMedium,
                color = MovieMateColors.SecondaryText,
                letterSpacing = 0.sp
            )
        }

        OpeningStatus(
            message = errorMessage ?: message,
            isError = errorMessage != null,
            onRetryClicked = onRetryClicked,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = StatusOffsetY)
        )
    }
}

@Composable
private fun OpeningStatus(
    message: String,
    isError: Boolean,
    onRetryClicked: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isError) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(SpinnerSize)
                    .semantics { contentDescription = "MovieMate wird geladen" },
                color = MovieMateColors.AccentLight,
                strokeWidth = SpinnerStrokeWidth
            )
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = if (isError) MovieMateColors.Rating else MovieMateColors.SecondaryText,
            textAlign = TextAlign.Center,
            letterSpacing = 0.sp,
            modifier = Modifier.padding(top = StatusMessageTopSpacing)
        )

        if (isError && onRetryClicked != null) {
            Spacer(modifier = Modifier.height(RetryButtonTopSpacing))
            Button(
                onClick = onRetryClicked,
                shape = RoundedCornerShape(MovieMateShapes.controlRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MovieMateColors.Accent,
                    contentColor = MovieMateColors.PrimaryText
                )
            ) {
                Text(
                    text = "Erneut versuchen",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OpeningLogo() {
    val shape = RoundedCornerShape(MovieMateShapes.cardRadius + 10.dp)

    Surface(
        modifier = Modifier.size(LogoContainerSize),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(
            width = MovieMateSpacing.dividerThickness,
            color = MovieMateColors.AccentLight.copy(alpha = LogoBorderAlpha)
        ),
        tonalElevation = MovieMateSpacing.noElevation,
        shadowElevation = MovieMateSpacing.noElevation
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(LogoSize)
                    .clip(shape)
            )
        }
    }
}

@Composable
private fun MovieMateWordmark() {
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = MovieMateColors.PrimaryText,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Movie")
            }
            withStyle(
                SpanStyle(
                    color = MovieMateColors.AccentLight,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Mate")
            }
        },
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    )
}

@Composable
private fun OpeningFilmReels(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawOpeningGlow(
            center = Offset(size.width * 0.08f, size.height * 0.10f),
            radius = size.minDimension * 0.56f,
            alpha = 0.20f
        )
        drawFilmReel(
            center = Offset(size.width * 0.95f, size.height * 0.24f),
            radius = size.minDimension * 0.29f,
            alpha = 0.13f
        )
        drawFilmReel(
            center = Offset(size.width * 0.07f, size.height * 0.86f),
            radius = size.minDimension * 0.27f,
            alpha = 0.14f
        )
    }
}

private fun DrawScope.drawOpeningGlow(
    center: Offset,
    radius: Float,
    alpha: Float
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                MovieMateColors.AccentDark.copy(alpha = alpha),
                Color.Transparent
            ),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

private fun DrawScope.drawFilmReel(
    center: Offset,
    radius: Float,
    alpha: Float
) {
    val reelColor = MovieMateColors.AccentDark.copy(alpha = alpha)
    val cutoutColor = MovieMateColors.ScreenBackground.copy(alpha = 0.62f)
    val ringStroke = Stroke(width = radius * 0.012f)

    drawCircle(
        color = reelColor,
        radius = radius,
        center = center
    )
    drawCircle(
        color = MovieMateColors.AccentLight.copy(alpha = alpha * 0.32f),
        radius = radius,
        center = center,
        style = ringStroke
    )
    drawCircle(
        color = cutoutColor,
        radius = radius * 0.22f,
        center = center
    )

    val holeRadius = radius * 0.17f
    val holeDistance = radius * 0.53f
    listOf(
        Offset(center.x, center.y - holeDistance),
        Offset(center.x + holeDistance, center.y),
        Offset(center.x, center.y + holeDistance),
        Offset(center.x - holeDistance, center.y)
    ).forEach { holeCenter ->
        drawCircle(
            color = cutoutColor,
            radius = holeRadius,
            center = holeCenter
        )
    }
}

private val BrandOffsetY = (-56).dp
private val StatusOffsetY = 150.dp
private val LogoContainerSize = 126.dp
private val LogoSize = 118.dp
private val SpinnerSize = 38.dp
private val SpinnerStrokeWidth = 4.dp
private val LogoTitleSpacing = 24.dp
private val ClaimTopSpacing = 8.dp
private val StatusMessageTopSpacing = 16.dp
private val RetryButtonTopSpacing = 16.dp
private const val LogoBorderAlpha = 0.28f
