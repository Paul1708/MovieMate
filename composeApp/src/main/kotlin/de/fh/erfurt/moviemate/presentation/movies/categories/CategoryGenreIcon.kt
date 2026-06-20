package de.fh.erfurt.moviemate.presentation.movies.categories

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

/**
 * Lightweight vector icon for a category tile.
 *
 * Keeping the drawings local avoids adding an icon dependency while the UI is
 * still mock-driven. The mapping can be replaced with real assets later.
 */
@Composable
internal fun CategoryGenreIcon(
    iconKey: String?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val color = MovieMateColors.AccentLight
        val stroke = Stroke(
            width = size.minDimension * 0.075f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        when (iconKey.categoryIconOption().style) {
            CategoryIconStyle.Spark -> drawSparkIcon(color = color, stroke = stroke)
            CategoryIconStyle.Map -> drawMapIcon(color = color, stroke = stroke)
            CategoryIconStyle.Reel -> drawReelIcon(color = color, stroke = stroke)
            CategoryIconStyle.Document -> drawDocumentIcon(color = color, stroke = stroke)
            CategoryIconStyle.Smile -> drawSmileIcon(color = color, stroke = stroke)
            CategoryIconStyle.Lens -> drawLensIcon(color = color, stroke = stroke)
            CategoryIconStyle.Frame -> drawFrameIcon(color = color, stroke = stroke)
            CategoryIconStyle.Crystal -> drawCrystalIcon(color = color, stroke = stroke)
            CategoryIconStyle.Orbit -> drawOrbitIcon(color = color, stroke = stroke)
            CategoryIconStyle.Arc -> drawArcIcon(color = color, stroke = stroke)
            CategoryIconStyle.Grid -> drawGridIcon(color = color, stroke = stroke)
        }
    }
}

private fun DrawScope.drawSparkIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.52f, h * 0.08f)
        lineTo(w * 0.40f, h * 0.43f)
        lineTo(w * 0.60f, h * 0.43f)
        lineTo(w * 0.48f, h * 0.92f)
    }
    drawPath(path = path, color = color, style = stroke)
    drawLine(
        color = color,
        start = Offset(w * 0.18f, h * 0.34f),
        end = Offset(w * 0.30f, h * 0.44f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = color,
        start = Offset(w * 0.78f, h * 0.58f),
        end = Offset(w * 0.90f, h * 0.70f),
        strokeWidth = stroke.width,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawMapIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.16f, h * 0.18f),
        size = Size(w * 0.68f, h * 0.62f),
        cornerRadius = CornerRadius(w * 0.08f, h * 0.08f),
        style = stroke
    )
    drawLine(color, Offset(w * 0.38f, h * 0.22f), Offset(w * 0.38f, h * 0.76f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(w * 0.62f, h * 0.22f), Offset(w * 0.62f, h * 0.76f), stroke.width, StrokeCap.Round)
    drawCircle(color = color, radius = w * 0.045f, center = Offset(w * 0.30f, h * 0.34f))
    drawCircle(color = color, radius = w * 0.045f, center = Offset(w * 0.70f, h * 0.62f))
}

private fun DrawScope.drawReelIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    val center = Offset(w * 0.48f, h * 0.48f)
    drawCircle(color = color, radius = w * 0.30f, center = center, style = stroke)
    drawCircle(color = color, radius = w * 0.055f, center = center)
    drawCircle(color = color, radius = w * 0.045f, center = Offset(w * 0.48f, h * 0.28f))
    drawCircle(color = color, radius = w * 0.045f, center = Offset(w * 0.30f, h * 0.48f))
    drawCircle(color = color, radius = w * 0.045f, center = Offset(w * 0.66f, h * 0.48f))
    drawLine(color, Offset(w * 0.62f, h * 0.68f), Offset(w * 0.86f, h * 0.84f), stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawDocumentIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.24f, h * 0.12f),
        size = Size(w * 0.52f, h * 0.76f),
        cornerRadius = CornerRadius(w * 0.06f, h * 0.06f),
        style = stroke
    )
    drawLine(color, Offset(w * 0.36f, h * 0.36f), Offset(w * 0.64f, h * 0.36f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(w * 0.36f, h * 0.52f), Offset(w * 0.64f, h * 0.52f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(w * 0.36f, h * 0.68f), Offset(w * 0.56f, h * 0.68f), stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawSmileIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.18f, h * 0.18f),
        size = Size(w * 0.64f, h * 0.64f),
        cornerRadius = CornerRadius(w * 0.18f, h * 0.18f),
        style = stroke
    )
    drawCircle(color = color, radius = w * 0.04f, center = Offset(w * 0.40f, h * 0.44f))
    drawCircle(color = color, radius = w * 0.04f, center = Offset(w * 0.60f, h * 0.44f))
    drawArc(
        color = color,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(w * 0.36f, h * 0.44f),
        size = Size(w * 0.28f, h * 0.22f),
        style = stroke
    )
}

private fun DrawScope.drawLensIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    drawCircle(
        color = color,
        radius = w * 0.25f,
        center = Offset(w * 0.44f, h * 0.42f),
        style = stroke
    )
    drawLine(color, Offset(w * 0.62f, h * 0.62f), Offset(w * 0.82f, h * 0.82f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(w * 0.36f, h * 0.42f), Offset(w * 0.52f, h * 0.42f), stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawFrameIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.18f, h * 0.18f),
        size = Size(w * 0.64f, h * 0.64f),
        cornerRadius = CornerRadius(w * 0.06f, h * 0.06f),
        style = stroke
    )
    val path = Path().apply {
        moveTo(w * 0.26f, h * 0.66f)
        lineTo(w * 0.42f, h * 0.50f)
        lineTo(w * 0.54f, h * 0.60f)
        lineTo(w * 0.68f, h * 0.42f)
        lineTo(w * 0.76f, h * 0.66f)
    }
    drawPath(path = path, color = color, style = stroke)
    drawCircle(color = color, radius = w * 0.04f, center = Offset(w * 0.36f, h * 0.34f))
}

private fun DrawScope.drawCrystalIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.50f, h * 0.10f)
        lineTo(w * 0.82f, h * 0.42f)
        lineTo(w * 0.50f, h * 0.90f)
        lineTo(w * 0.18f, h * 0.42f)
        close()
    }
    drawPath(path = path, color = color, style = stroke)
    drawLine(color, Offset(w * 0.18f, h * 0.42f), Offset(w * 0.82f, h * 0.42f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(w * 0.50f, h * 0.10f), Offset(w * 0.50f, h * 0.90f), stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawOrbitIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    drawCircle(color = color, radius = w * 0.055f, center = Offset(w * 0.50f, h * 0.50f))
    drawOval(
        color = color,
        topLeft = Offset(w * 0.16f, h * 0.36f),
        size = Size(w * 0.68f, h * 0.28f),
        style = stroke
    )
    drawOval(
        color = color,
        topLeft = Offset(w * 0.36f, h * 0.16f),
        size = Size(w * 0.28f, h * 0.68f),
        style = stroke
    )
    drawLine(color, Offset(w * 0.24f, h * 0.78f), Offset(w * 0.76f, h * 0.22f), stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawArcIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    drawArc(
        color = color,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(w * 0.20f, h * 0.18f),
        size = Size(w * 0.60f, h * 0.58f),
        style = stroke
    )
    drawLine(color, Offset(w * 0.26f, h * 0.54f), Offset(w * 0.26f, h * 0.74f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(w * 0.74f, h * 0.54f), Offset(w * 0.74f, h * 0.74f), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(w * 0.40f, h * 0.82f), Offset(w * 0.60f, h * 0.82f), stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawGridIcon(
    color: Color,
    stroke: Stroke
) {
    val w = size.width
    val h = size.height
    val tile = Size(w * 0.22f, h * 0.22f)
    val radius = CornerRadius(w * 0.04f, h * 0.04f)
    listOf(
        Offset(w * 0.22f, h * 0.22f),
        Offset(w * 0.56f, h * 0.22f),
        Offset(w * 0.22f, h * 0.56f),
        Offset(w * 0.56f, h * 0.56f)
    ).forEach { topLeft ->
        drawRoundRect(
            color = color,
            topLeft = topLeft,
            size = tile,
            cornerRadius = radius,
            style = stroke
        )
    }
}
