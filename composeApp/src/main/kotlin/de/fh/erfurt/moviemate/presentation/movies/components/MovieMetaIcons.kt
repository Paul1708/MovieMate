package de.fh.erfurt.moviemate.presentation.movies.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

@Composable
internal fun MovieRuntimeIcon(
    modifier: Modifier = Modifier
) {
    MovieMetaCanvasIcon(
        modifier = modifier,
        drawIcon = { palette -> drawClockIcon(palette) }
    )
}

@Composable
internal fun MovieDateIcon(
    modifier: Modifier = Modifier
) {
    MovieMetaCanvasIcon(
        modifier = modifier,
        drawIcon = { palette -> drawDateIcon(palette) }
    )
}

@Composable
private fun MovieMetaCanvasIcon(
    modifier: Modifier,
    drawIcon: DrawScope.(MovieMetaIconPalette) -> Unit,
) {
    Canvas(modifier = modifier) {
        drawIcon(
            MovieMetaIconPalette(
                primary = MovieMateColors.AccentLight,
                secondary = MovieMateColors.Accent,
                stroke = Stroke(
                    width = size.minDimension * 0.11f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
                thinStroke = Stroke(
                    width = size.minDimension * 0.075f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        )
    }
}

private data class MovieMetaIconPalette(
    val primary: Color,
    val secondary: Color,
    val stroke: Stroke,
    val thinStroke: Stroke,
)

private fun DrawScope.drawClockIcon(palette: MovieMetaIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(
        color = palette.primary,
        radius = w * 0.34f,
        center = Offset(w * 0.50f, h * 0.50f),
        style = palette.stroke
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.50f, h * 0.50f),
        end = Offset(w * 0.50f, h * 0.29f),
        strokeWidth = palette.thinStroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.secondary,
        start = Offset(w * 0.50f, h * 0.50f),
        end = Offset(w * 0.66f, h * 0.58f),
        strokeWidth = palette.thinStroke.width,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = palette.secondary,
        radius = w * 0.035f,
        center = Offset(w * 0.50f, h * 0.50f)
    )
}

private fun DrawScope.drawDateIcon(palette: MovieMetaIconPalette) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = palette.primary,
        topLeft = Offset(w * 0.18f, h * 0.22f),
        size = Size(w * 0.64f, h * 0.58f),
        cornerRadius = CornerRadius(w * 0.10f, h * 0.10f),
        style = palette.stroke
    )
    drawLine(
        color = palette.secondary,
        start = Offset(w * 0.20f, h * 0.40f),
        end = Offset(w * 0.80f, h * 0.40f),
        strokeWidth = palette.thinStroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.34f, h * 0.16f),
        end = Offset(w * 0.34f, h * 0.29f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.66f, h * 0.16f),
        end = Offset(w * 0.66f, h * 0.29f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
    drawCircle(palette.secondary, w * 0.032f, Offset(w * 0.38f, h * 0.55f))
    drawCircle(palette.secondary, w * 0.032f, Offset(w * 0.50f, h * 0.55f))
    drawCircle(palette.secondary, w * 0.032f, Offset(w * 0.62f, h * 0.55f))
}
