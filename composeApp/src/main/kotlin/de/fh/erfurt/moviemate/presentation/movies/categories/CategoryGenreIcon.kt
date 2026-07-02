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

@Composable
internal fun CategoryGenreIcon(
    iconKey: String?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val palette = CategoryIconPalette(
            primary = MovieMateColors.AccentLight,
            secondary = MovieMateColors.Accent,
            stroke = Stroke(
                width = size.minDimension * 0.075f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            ),
            thinStroke = Stroke(
                width = size.minDimension * 0.052f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            ),
            heavyStroke = Stroke(
                width = size.minDimension * 0.105f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        when (iconKey.categoryIconOption().style) {
            CategoryIconStyle.Action -> drawActionIcon(palette)
            CategoryIconStyle.Comedy -> drawComedyIcon(palette)
            CategoryIconStyle.Drama -> drawDramaIcon(palette)
            CategoryIconStyle.Theater -> drawTheaterIcon(palette)
            CategoryIconStyle.Romance -> drawRomanceIcon(palette)
            CategoryIconStyle.Fantasy -> drawFantasyIcon(palette)
            CategoryIconStyle.Adventure -> drawAdventureIcon(palette)
            CategoryIconStyle.ScienceFiction -> drawScienceFictionIcon(palette)
            CategoryIconStyle.Thriller -> drawThrillerIcon(palette)
            CategoryIconStyle.Horror -> drawHorrorIcon(palette)
            CategoryIconStyle.Mystery -> drawMysteryIcon(palette)
            CategoryIconStyle.Crime -> drawCrimeIcon(palette)
            CategoryIconStyle.History -> drawHistoryIcon(palette)
            CategoryIconStyle.Documentary -> drawDocumentaryIcon(palette)
            CategoryIconStyle.Musical -> drawMusicalIcon(palette)
            CategoryIconStyle.Biography -> drawBiographyIcon(palette)
            CategoryIconStyle.Family -> drawFamilyIcon(palette)
            CategoryIconStyle.Animation -> drawAnimationIcon(palette)
            CategoryIconStyle.Kids -> drawKidsIcon(palette)
            CategoryIconStyle.Gaming -> drawGamingIcon(palette)
            CategoryIconStyle.Superhero -> drawSuperheroIcon(palette)
            CategoryIconStyle.War -> drawWarIcon(palette)
            CategoryIconStyle.Western -> drawWesternIcon(palette)
            CategoryIconStyle.RoadMovie -> drawRoadMovieIcon(palette)
            CategoryIconStyle.Nature -> drawNatureIcon(palette)
            CategoryIconStyle.Sport -> drawSportIcon(palette)
            CategoryIconStyle.Magic -> drawMagicIcon(palette)
            CategoryIconStyle.ArtHouse -> drawArtHouseIcon(palette)
            CategoryIconStyle.MindBending -> drawMindBendingIcon(palette)
            CategoryIconStyle.TimeTravel -> drawTimeTravelIcon(palette)
            CategoryIconStyle.Cult -> drawCultIcon(palette)
            CategoryIconStyle.Disaster -> drawDisasterIcon(palette)
            CategoryIconStyle.Survival -> drawSurvivalIcon(palette)
            CategoryIconStyle.Other -> drawOtherIcon(palette)
        }
    }
}

private data class CategoryIconPalette(
    val primary: Color,
    val secondary: Color,
    val stroke: Stroke,
    val thinStroke: Stroke,
    val heavyStroke: Stroke,
)

private fun DrawScope.drawActionIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = palette.secondary,
        topLeft = Offset(w * 0.16f, h * 0.30f),
        size = Size(w * 0.68f, h * 0.50f),
        cornerRadius = CornerRadius(w * 0.035f, h * 0.035f),
        style = palette.stroke
    )
    drawLine(palette.primary, Offset(w * 0.18f, h * 0.32f), Offset(w * 0.82f, h * 0.18f), palette.stroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.26f, h * 0.30f), Offset(w * 0.36f, h * 0.48f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.47f, h * 0.25f), Offset(w * 0.58f, h * 0.42f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.68f, h * 0.21f), Offset(w * 0.78f, h * 0.36f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.32f, h * 0.58f), Offset(w * 0.68f, h * 0.58f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.32f, h * 0.70f), Offset(w * 0.58f, h * 0.70f), palette.thinStroke.width, StrokeCap.Round)
}

private fun DrawScope.drawComedyIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    val mask = Path().apply {
        moveTo(w * 0.24f, h * 0.22f)
        quadraticTo(w * 0.50f, h * 0.13f, w * 0.76f, h * 0.22f)
        lineTo(w * 0.76f, h * 0.52f)
        cubicTo(w * 0.74f, h * 0.74f, w * 0.56f, h * 0.86f, w * 0.50f, h * 0.88f)
        cubicTo(w * 0.44f, h * 0.86f, w * 0.26f, h * 0.74f, w * 0.24f, h * 0.52f)
        close()
    }
    drawPath(mask, palette.secondary)
    drawPath(mask, palette.primary, style = palette.thinStroke)
    drawArc(
        color = MovieMateColors.CardBackground,
        startAngle = 10f,
        sweepAngle = 160f,
        useCenter = false,
        topLeft = Offset(w * 0.36f, h * 0.50f),
        size = Size(w * 0.28f, h * 0.17f),
        style = palette.thinStroke
    )
    drawOval(MovieMateColors.CardBackground, Offset(w * 0.34f, h * 0.42f), Size(w * 0.10f, h * 0.06f))
    drawOval(MovieMateColors.CardBackground, Offset(w * 0.56f, h * 0.42f), Size(w * 0.10f, h * 0.06f))
}

private fun DrawScope.drawDramaIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawMask(
        palette = palette,
        topLeft = Offset(w * 0.22f, h * 0.23f),
        width = w * 0.34f,
        height = h * 0.46f,
        sad = true
    )
    drawMask(
        palette = palette,
        topLeft = Offset(w * 0.44f, h * 0.34f),
        width = w * 0.34f,
        height = h * 0.46f,
        sad = false
    )
}

private fun DrawScope.drawTheaterIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = palette.secondary,
        topLeft = Offset(w * 0.24f, h * 0.20f),
        size = Size(w * 0.18f, h * 0.56f),
        cornerRadius = CornerRadius(w * 0.02f, h * 0.02f)
    )
    drawRoundRect(
        color = palette.primary,
        topLeft = Offset(w * 0.58f, h * 0.20f),
        size = Size(w * 0.18f, h * 0.56f),
        cornerRadius = CornerRadius(w * 0.02f, h * 0.02f)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.42f, h * 0.20f)
            cubicTo(w * 0.50f, h * 0.38f, w * 0.48f, h * 0.60f, w * 0.32f, h * 0.76f)
            lineTo(w * 0.52f, h * 0.76f)
            cubicTo(w * 0.58f, h * 0.58f, w * 0.57f, h * 0.38f, w * 0.52f, h * 0.20f)
            close()
        },
        color = palette.primary
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.58f, h * 0.20f)
            cubicTo(w * 0.50f, h * 0.38f, w * 0.52f, h * 0.60f, w * 0.68f, h * 0.76f)
            lineTo(w * 0.48f, h * 0.76f)
            cubicTo(w * 0.42f, h * 0.58f, w * 0.43f, h * 0.38f, w * 0.48f, h * 0.20f)
            close()
        },
        color = palette.secondary
    )
    drawLine(palette.primary, Offset(w * 0.23f, h * 0.20f), Offset(w * 0.77f, h * 0.20f), palette.thinStroke.width, StrokeCap.Round)
}

private fun DrawScope.drawRomanceIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    val heart = Path().apply {
        moveTo(w * 0.50f, h * 0.80f)
        cubicTo(w * 0.26f, h * 0.63f, w * 0.18f, h * 0.49f, w * 0.22f, h * 0.34f)
        cubicTo(w * 0.27f, h * 0.17f, w * 0.45f, h * 0.20f, w * 0.50f, h * 0.34f)
        cubicTo(w * 0.55f, h * 0.20f, w * 0.73f, h * 0.17f, w * 0.78f, h * 0.34f)
        cubicTo(w * 0.82f, h * 0.49f, w * 0.74f, h * 0.63f, w * 0.50f, h * 0.80f)
    }
    drawPath(heart, palette.primary, style = palette.stroke)
}

private fun DrawScope.drawFantasyIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.42f, h * 0.24f)
            cubicTo(w * 0.32f, h * 0.40f, w * 0.24f, h * 0.58f, w * 0.18f, h * 0.74f)
            cubicTo(w * 0.40f, h * 0.84f, w * 0.62f, h * 0.84f, w * 0.84f, h * 0.74f)
            cubicTo(w * 0.74f, h * 0.58f, w * 0.66f, h * 0.40f, w * 0.58f, h * 0.24f)
            cubicTo(w * 0.52f, h * 0.15f, w * 0.46f, h * 0.15f, w * 0.42f, h * 0.24f)
            close()
        },
        color = palette.secondary
    )
    drawLine(palette.primary, Offset(w * 0.50f, h * 0.18f), Offset(w * 0.76f, h * 0.70f), palette.thinStroke.width, StrokeCap.Round)
    drawCircle(palette.primary, w * 0.028f, Offset(w * 0.34f, h * 0.47f))
    drawCircle(palette.primary, w * 0.024f, Offset(w * 0.62f, h * 0.58f))
    drawSparkle(palette.primary, w * 0.18f, h * 0.31f, w * 0.045f, palette.thinStroke.width)
    drawSparkle(palette.primary, w * 0.82f, h * 0.39f, w * 0.035f, palette.thinStroke.width)
}

private fun DrawScope.drawAdventureIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.80f, h * 0.15f)
            lineTo(w * 0.88f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.24f)
            lineTo(w * 0.35f, h * 0.78f)
            lineTo(w * 0.27f, h * 0.70f)
            close()
        },
        color = palette.secondary
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.37f, h * 0.69f),
        end = Offset(w * 0.79f, h * 0.24f),
        strokeWidth = palette.thinStroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.18f, h * 0.66f),
        end = Offset(w * 0.44f, h * 0.87f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.32f, h * 0.79f),
        end = Offset(w * 0.20f, h * 0.91f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = palette.secondary,
        radius = w * 0.045f,
        center = Offset(w * 0.17f, h * 0.93f),
        style = palette.thinStroke
    )
}

private fun DrawScope.drawScienceFictionIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.30f, h * 0.67f)
            cubicTo(w * 0.33f, h * 0.40f, w * 0.58f, h * 0.20f, w * 0.79f, h * 0.20f)
            cubicTo(w * 0.84f, h * 0.43f, w * 0.66f, h * 0.68f, w * 0.39f, h * 0.74f)
            close()
        },
        color = palette.secondary
    )
    drawCircle(palette.primary, w * 0.052f, Offset(w * 0.60f, h * 0.38f))
    drawPath(
        path = Path().apply {
            moveTo(w * 0.33f, h * 0.61f)
            lineTo(w * 0.14f, h * 0.76f)
            lineTo(w * 0.36f, h * 0.75f)
            close()
        },
        color = palette.primary
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.43f, h * 0.73f)
            lineTo(w * 0.32f, h * 0.92f)
            lineTo(w * 0.56f, h * 0.80f)
            close()
        },
        color = palette.primary
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.31f, h * 0.72f)
            lineTo(w * 0.22f, h * 0.89f)
            lineTo(w * 0.39f, h * 0.78f)
            close()
        },
        color = palette.secondary
    )
}

private fun DrawScope.drawThrillerIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawOval(
        color = palette.secondary,
        topLeft = Offset(w * 0.18f, h * 0.38f),
        size = Size(w * 0.64f, h * 0.22f)
    )
    drawOval(
        color = palette.primary,
        topLeft = Offset(w * 0.27f, h * 0.28f),
        size = Size(w * 0.46f, h * 0.24f),
        style = palette.thinStroke
    )
    drawCircle(palette.primary, w * 0.025f, Offset(w * 0.42f, h * 0.40f))
    drawCircle(palette.primary, w * 0.025f, Offset(w * 0.56f, h * 0.39f))
    drawLine(palette.primary, Offset(w * 0.38f, h * 0.70f), Offset(w * 0.35f, h * 0.82f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.50f, h * 0.70f), Offset(w * 0.50f, h * 0.84f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.62f, h * 0.70f), Offset(w * 0.65f, h * 0.82f), palette.thinStroke.width, StrokeCap.Round)
}

private fun DrawScope.drawHorrorIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.22f, h * 0.46f)
            cubicTo(w * 0.22f, h * 0.25f, w * 0.38f, h * 0.16f, w * 0.50f, h * 0.16f)
            cubicTo(w * 0.66f, h * 0.16f, w * 0.78f, h * 0.30f, w * 0.78f, h * 0.48f)
            lineTo(w * 0.72f, h * 0.72f)
            lineTo(w * 0.64f, h * 0.68f)
            lineTo(w * 0.58f, h * 0.80f)
            lineTo(w * 0.50f, h * 0.69f)
            lineTo(w * 0.42f, h * 0.80f)
            lineTo(w * 0.36f, h * 0.68f)
            lineTo(w * 0.28f, h * 0.72f)
            close()
        },
        color = palette.secondary
    )
    drawCircle(MovieMateColors.CardBackground, w * 0.055f, Offset(w * 0.40f, h * 0.45f))
    drawCircle(MovieMateColors.CardBackground, w * 0.055f, Offset(w * 0.60f, h * 0.45f))
    drawPath(
        path = Path().apply {
            moveTo(w * 0.50f, h * 0.52f)
            lineTo(w * 0.45f, h * 0.62f)
            lineTo(w * 0.55f, h * 0.62f)
            close()
        },
        color = MovieMateColors.CardBackground
    )
}

private fun DrawScope.drawMysteryIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.29f, h * 0.42f)
            cubicTo(w * 0.35f, h * 0.26f, w * 0.65f, h * 0.26f, w * 0.71f, h * 0.42f)
            lineTo(w * 0.61f, h * 0.47f)
            cubicTo(w * 0.54f, h * 0.44f, w * 0.46f, h * 0.44f, w * 0.39f, h * 0.47f)
            close()
        },
        color = palette.secondary
    )
    drawLine(palette.primary, Offset(w * 0.25f, h * 0.46f), Offset(w * 0.75f, h * 0.46f), palette.stroke.width, StrokeCap.Round)
    drawCircle(
        color = palette.primary,
        radius = w * 0.14f,
        center = Offset(w * 0.58f, h * 0.62f),
        style = palette.stroke
    )
    drawLine(palette.primary, Offset(w * 0.67f, h * 0.73f), Offset(w * 0.82f, h * 0.86f), palette.stroke.width, StrokeCap.Round)
    drawCircle(palette.primary, w * 0.025f, Offset(w * 0.58f, h * 0.62f))
}

private fun DrawScope.drawCrimeIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.35f, h * 0.32f)
            cubicTo(w * 0.42f, h * 0.22f, w * 0.60f, h * 0.22f, w * 0.67f, h * 0.32f)
            lineTo(w * 0.72f, h * 0.48f)
            cubicTo(w * 0.58f, h * 0.43f, w * 0.42f, h * 0.43f, w * 0.28f, h * 0.48f)
            close()
        },
        color = palette.secondary
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.20f, h * 0.50f)
            cubicTo(w * 0.34f, h * 0.43f, w * 0.66f, h * 0.43f, w * 0.80f, h * 0.50f)
            cubicTo(w * 0.68f, h * 0.57f, w * 0.32f, h * 0.57f, w * 0.20f, h * 0.50f)
            close()
        },
        color = palette.secondary
    )
    drawLine(palette.primary, Offset(w * 0.31f, h * 0.43f), Offset(w * 0.70f, h * 0.43f), palette.thinStroke.width, StrokeCap.Round)
    drawRoundRect(
        color = palette.primary,
        topLeft = Offset(w * 0.31f, h * 0.63f),
        size = Size(w * 0.16f, h * 0.08f),
        cornerRadius = CornerRadius(w * 0.035f, h * 0.035f),
        style = palette.thinStroke
    )
    drawRoundRect(
        color = palette.primary,
        topLeft = Offset(w * 0.53f, h * 0.63f),
        size = Size(w * 0.16f, h * 0.08f),
        cornerRadius = CornerRadius(w * 0.035f, h * 0.035f),
        style = palette.thinStroke
    )
    drawLine(palette.primary, Offset(w * 0.47f, h * 0.67f), Offset(w * 0.53f, h * 0.67f), palette.thinStroke.width, StrokeCap.Round)
}

private fun DrawScope.drawHistoryIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = palette.secondary,
        topLeft = Offset(w * 0.30f, h * 0.18f),
        size = Size(w * 0.40f, h * 0.08f),
        cornerRadius = CornerRadius(w * 0.04f, h * 0.04f)
    )
    drawRoundRect(
        color = palette.secondary,
        topLeft = Offset(w * 0.30f, h * 0.74f),
        size = Size(w * 0.40f, h * 0.08f),
        cornerRadius = CornerRadius(w * 0.04f, h * 0.04f)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.36f, h * 0.26f)
            lineTo(w * 0.64f, h * 0.26f)
            cubicTo(w * 0.62f, h * 0.40f, w * 0.54f, h * 0.44f, w * 0.50f, h * 0.50f)
            cubicTo(w * 0.46f, h * 0.56f, w * 0.38f, h * 0.60f, w * 0.36f, h * 0.74f)
            lineTo(w * 0.64f, h * 0.74f)
            cubicTo(w * 0.62f, h * 0.60f, w * 0.54f, h * 0.56f, w * 0.50f, h * 0.50f)
            cubicTo(w * 0.46f, h * 0.44f, w * 0.38f, h * 0.40f, w * 0.36f, h * 0.26f)
        },
        color = palette.primary,
        style = palette.stroke
    )
    drawCircle(palette.secondary, w * 0.023f, Offset(w * 0.50f, h * 0.50f))
}

private fun DrawScope.drawDocumentaryIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(
        color = palette.primary,
        radius = w * 0.31f,
        center = Offset(w * 0.50f, h * 0.50f),
        style = palette.stroke
    )
    drawOval(
        color = palette.primary,
        topLeft = Offset(w * 0.33f, h * 0.19f),
        size = Size(w * 0.34f, h * 0.62f),
        style = palette.thinStroke
    )
    drawLine(palette.secondary, Offset(w * 0.26f, h * 0.38f), Offset(w * 0.74f, h * 0.38f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.secondary, Offset(w * 0.20f, h * 0.50f), Offset(w * 0.80f, h * 0.50f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.secondary, Offset(w * 0.26f, h * 0.62f), Offset(w * 0.74f, h * 0.62f), palette.thinStroke.width, StrokeCap.Round)
}

private fun DrawScope.drawMusicalIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(palette.secondary, w * 0.11f, Offset(w * 0.34f, h * 0.68f))
    drawCircle(palette.primary, w * 0.11f, Offset(w * 0.68f, h * 0.62f))
    drawLine(palette.primary, Offset(w * 0.45f, h * 0.68f), Offset(w * 0.45f, h * 0.28f), palette.stroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.79f, h * 0.62f), Offset(w * 0.79f, h * 0.22f), palette.stroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.45f, h * 0.28f), Offset(w * 0.79f, h * 0.22f), palette.stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawBiographyIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = palette.secondary,
        topLeft = Offset(w * 0.28f, h * 0.16f),
        size = Size(w * 0.48f, h * 0.68f),
        cornerRadius = CornerRadius(w * 0.04f, h * 0.04f)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.30f, h * 0.52f)
            lineTo(w * 0.52f, h * 0.68f)
            lineTo(w * 0.74f, h * 0.52f)
            lineTo(w * 0.74f, h * 0.84f)
            lineTo(w * 0.30f, h * 0.84f)
            close()
        },
        color = palette.primary
    )
}

private fun DrawScope.drawFamilyIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(palette.primary, w * 0.075f, Offset(w * 0.36f, h * 0.28f))
    drawCircle(palette.primary, w * 0.075f, Offset(w * 0.64f, h * 0.28f))
    drawCircle(palette.secondary, w * 0.06f, Offset(w * 0.50f, h * 0.46f))
    drawLine(palette.primary, Offset(w * 0.34f, h * 0.40f), Offset(w * 0.28f, h * 0.72f), palette.heavyStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.66f, h * 0.40f), Offset(w * 0.72f, h * 0.72f), palette.heavyStroke.width, StrokeCap.Round)
    drawLine(palette.secondary, Offset(w * 0.50f, h * 0.54f), Offset(w * 0.50f, h * 0.80f), palette.stroke.width, StrokeCap.Round)
    drawLine(palette.secondary, Offset(w * 0.42f, h * 0.63f), Offset(w * 0.58f, h * 0.63f), palette.stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawAnimationIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(palette.secondary, w * 0.22f, Offset(w * 0.50f, h * 0.44f))
    drawCircle(MovieMateColors.CardBackground, w * 0.035f, Offset(w * 0.42f, h * 0.40f))
    drawCircle(MovieMateColors.CardBackground, w * 0.035f, Offset(w * 0.58f, h * 0.40f))
    drawArc(
        color = MovieMateColors.CardBackground,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(w * 0.40f, h * 0.44f),
        size = Size(w * 0.20f, h * 0.14f),
        style = palette.thinStroke
    )
    drawCircle(palette.primary, w * 0.08f, Offset(w * 0.34f, h * 0.70f), style = palette.stroke)
    drawCircle(palette.primary, w * 0.08f, Offset(w * 0.66f, h * 0.70f), style = palette.stroke)
    drawLine(palette.primary, Offset(w * 0.42f, h * 0.68f), Offset(w * 0.58f, h * 0.68f), palette.thinStroke.width, StrokeCap.Round)
}

private fun DrawScope.drawKidsIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(palette.primary, w * 0.30f, Offset(w * 0.50f, h * 0.50f), style = palette.stroke)
    drawCircle(palette.primary, w * 0.035f, Offset(w * 0.40f, h * 0.43f))
    drawCircle(palette.primary, w * 0.035f, Offset(w * 0.60f, h * 0.43f))
    drawArc(
        color = palette.primary,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(w * 0.36f, h * 0.44f),
        size = Size(w * 0.28f, h * 0.24f),
        style = palette.stroke
    )
}

private fun DrawScope.drawGamingIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.24f, h * 0.38f)
            cubicTo(w * 0.30f, h * 0.30f, w * 0.70f, h * 0.30f, w * 0.76f, h * 0.38f)
            lineTo(w * 0.84f, h * 0.70f)
            cubicTo(w * 0.86f, h * 0.80f, w * 0.72f, h * 0.83f, w * 0.66f, h * 0.72f)
            lineTo(w * 0.34f, h * 0.72f)
            cubicTo(w * 0.28f, h * 0.83f, w * 0.14f, h * 0.80f, w * 0.16f, h * 0.70f)
            close()
        },
        color = palette.secondary
    )
    drawLine(palette.primary, Offset(w * 0.30f, h * 0.55f), Offset(w * 0.44f, h * 0.55f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.37f, h * 0.48f), Offset(w * 0.37f, h * 0.62f), palette.thinStroke.width, StrokeCap.Round)
    drawCircle(palette.primary, w * 0.028f, Offset(w * 0.62f, h * 0.51f))
    drawCircle(palette.primary, w * 0.028f, Offset(w * 0.71f, h * 0.58f))
}

private fun DrawScope.drawSuperheroIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawOval(
        color = palette.primary,
        topLeft = Offset(w * 0.16f, h * 0.35f),
        size = Size(w * 0.68f, h * 0.30f),
        style = palette.stroke
    )
    drawOval(
        color = palette.primary,
        topLeft = Offset(w * 0.35f, h * 0.16f),
        size = Size(w * 0.30f, h * 0.68f),
        style = palette.stroke
    )
    drawOval(
        color = palette.primary,
        topLeft = Offset(w * 0.24f, h * 0.24f),
        size = Size(w * 0.52f, h * 0.52f),
        style = palette.thinStroke
    )
    drawCircle(palette.secondary, w * 0.060f, Offset(w * 0.50f, h * 0.50f))
}

private fun DrawScope.drawWarIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.50f, h * 0.12f)
            lineTo(w * 0.80f, h * 0.26f)
            lineTo(w * 0.74f, h * 0.61f)
            cubicTo(w * 0.68f, h * 0.76f, w * 0.56f, h * 0.85f, w * 0.50f, h * 0.88f)
            cubicTo(w * 0.44f, h * 0.85f, w * 0.32f, h * 0.76f, w * 0.26f, h * 0.61f)
            lineTo(w * 0.20f, h * 0.26f)
            close()
        },
        color = palette.secondary
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.50f, h * 0.18f)
            lineTo(w * 0.72f, h * 0.29f)
            lineTo(w * 0.68f, h * 0.58f)
            cubicTo(w * 0.64f, h * 0.70f, w * 0.55f, h * 0.77f, w * 0.50f, h * 0.80f)
            cubicTo(w * 0.45f, h * 0.77f, w * 0.36f, h * 0.70f, w * 0.32f, h * 0.58f)
            lineTo(w * 0.28f, h * 0.29f)
            close()
        },
        color = palette.primary,
        style = palette.thinStroke
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.50f, h * 0.35f)
            lineTo(w * 0.57f, h * 0.47f)
            lineTo(w * 0.71f, h * 0.49f)
            lineTo(w * 0.61f, h * 0.59f)
            lineTo(w * 0.64f, h * 0.73f)
            lineTo(w * 0.50f, h * 0.66f)
            lineTo(w * 0.36f, h * 0.73f)
            lineTo(w * 0.39f, h * 0.59f)
            lineTo(w * 0.29f, h * 0.49f)
            lineTo(w * 0.43f, h * 0.47f)
            close()
        },
        color = MovieMateColors.CardBackground
    )
}

private fun DrawScope.drawWesternIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.34f, h * 0.42f)
            quadraticTo(w * 0.50f, h * 0.24f, w * 0.66f, h * 0.42f)
            lineTo(w * 0.82f, h * 0.56f)
            lineTo(w * 0.18f, h * 0.56f)
            close()
        },
        color = palette.secondary
    )
    drawLine(palette.primary, Offset(w * 0.24f, h * 0.58f), Offset(w * 0.76f, h * 0.58f), palette.stroke.width, StrokeCap.Round)
    drawPath(
        path = Path().apply {
            moveTo(w * 0.22f, h * 0.76f)
            lineTo(w * 0.34f, h * 0.52f)
            lineTo(w * 0.46f, h * 0.76f)
            close()
        },
        color = palette.primary
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.42f, h * 0.76f)
            lineTo(w * 0.54f, h * 0.44f)
            lineTo(w * 0.72f, h * 0.76f)
            close()
        },
        color = palette.secondary
    )
}

private fun DrawScope.drawRoadMovieIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = palette.secondary,
        topLeft = Offset(w * 0.17f, h * 0.44f),
        size = Size(w * 0.66f, h * 0.24f),
        cornerRadius = CornerRadius(w * 0.05f, h * 0.05f)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.31f, h * 0.44f)
            lineTo(w * 0.41f, h * 0.29f)
            lineTo(w * 0.61f, h * 0.29f)
            lineTo(w * 0.73f, h * 0.44f)
            close()
        },
        color = palette.primary
    )
    drawRoundRect(
        color = MovieMateColors.CardBackground,
        topLeft = Offset(w * 0.42f, h * 0.34f),
        size = Size(w * 0.14f, h * 0.08f),
        cornerRadius = CornerRadius(w * 0.02f, h * 0.02f)
    )
    drawLine(palette.primary, Offset(w * 0.18f, h * 0.58f), Offset(w * 0.82f, h * 0.58f), palette.thinStroke.width, StrokeCap.Round)
    drawCircle(MovieMateColors.CardBackground, w * 0.065f, Offset(w * 0.32f, h * 0.68f))
    drawCircle(MovieMateColors.CardBackground, w * 0.065f, Offset(w * 0.68f, h * 0.68f))
    drawCircle(palette.primary, w * 0.028f, Offset(w * 0.32f, h * 0.68f))
    drawCircle(palette.primary, w * 0.028f, Offset(w * 0.68f, h * 0.68f))
}

private fun DrawScope.drawNatureIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.34f, h * 0.76f)
            cubicTo(w * 0.30f, h * 0.46f, w * 0.48f, h * 0.24f, w * 0.76f, h * 0.20f)
            cubicTo(w * 0.82f, h * 0.48f, w * 0.64f, h * 0.76f, w * 0.34f, h * 0.76f)
            close()
        },
        color = palette.secondary
    )
    drawLine(palette.primary, Offset(w * 0.25f, h * 0.86f), Offset(w * 0.66f, h * 0.42f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.44f, h * 0.66f), Offset(w * 0.34f, h * 0.55f), palette.thinStroke.width, StrokeCap.Round)
}

private fun DrawScope.drawSportIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(palette.primary, w * 0.07f, Offset(w * 0.54f, h * 0.22f))
    drawLine(palette.secondary, Offset(w * 0.50f, h * 0.34f), Offset(w * 0.38f, h * 0.56f), palette.heavyStroke.width, StrokeCap.Round)
    drawLine(palette.secondary, Offset(w * 0.50f, h * 0.34f), Offset(w * 0.69f, h * 0.42f), palette.heavyStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.38f, h * 0.56f), Offset(w * 0.26f, h * 0.78f), palette.heavyStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.42f, h * 0.58f), Offset(w * 0.62f, h * 0.80f), palette.heavyStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.24f, h * 0.78f), Offset(w * 0.14f, h * 0.78f), palette.stroke.width, StrokeCap.Round)
}

private fun DrawScope.drawMagicIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(
        color = palette.primary,
        radius = w * 0.27f,
        center = Offset(w * 0.50f, h * 0.44f),
        style = palette.stroke
    )
    drawArc(
        color = palette.secondary,
        startAngle = 225f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(w * 0.33f, h * 0.26f),
        size = Size(w * 0.34f, h * 0.28f),
        style = palette.thinStroke
    )
    drawRoundRect(
        color = palette.secondary,
        topLeft = Offset(w * 0.37f, h * 0.70f),
        size = Size(w * 0.26f, h * 0.08f),
        cornerRadius = CornerRadius(w * 0.04f, h * 0.04f)
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.32f, h * 0.83f),
        end = Offset(w * 0.68f, h * 0.83f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawArtHouseIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.36f, h * 0.74f),
        end = Offset(w * 0.73f, h * 0.27f),
        strokeWidth = palette.heavyStroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = MovieMateColors.CardBackground,
        start = Offset(w * 0.41f, h * 0.68f),
        end = Offset(w * 0.68f, h * 0.34f),
        strokeWidth = palette.thinStroke.width,
        cap = StrokeCap.Round
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.26f, h * 0.86f)
            cubicTo(w * 0.17f, h * 0.72f, w * 0.27f, h * 0.57f, w * 0.43f, h * 0.58f)
            cubicTo(w * 0.46f, h * 0.72f, w * 0.40f, h * 0.83f, w * 0.26f, h * 0.86f)
            close()
        },
        color = palette.secondary
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.36f, h * 0.61f)
            lineTo(w * 0.48f, h * 0.49f)
            lineTo(w * 0.55f, h * 0.56f)
            lineTo(w * 0.43f, h * 0.68f)
            close()
        },
        color = palette.primary
    )
}

private fun DrawScope.drawMindBendingIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.31f, h * 0.47f)
            cubicTo(w * 0.31f, h * 0.25f, w * 0.69f, h * 0.25f, w * 0.69f, h * 0.47f)
            cubicTo(w * 0.69f, h * 0.58f, w * 0.59f, h * 0.64f, w * 0.56f, h * 0.72f)
            lineTo(w * 0.44f, h * 0.72f)
            cubicTo(w * 0.41f, h * 0.64f, w * 0.31f, h * 0.58f, w * 0.31f, h * 0.47f)
        },
        color = palette.primary,
        style = palette.stroke
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.42f, h * 0.49f)
            cubicTo(w * 0.45f, h * 0.35f, w * 0.61f, h * 0.38f, w * 0.58f, h * 0.50f)
            cubicTo(w * 0.56f, h * 0.58f, w * 0.49f, h * 0.58f, w * 0.49f, h * 0.66f)
        },
        color = palette.secondary,
        style = palette.thinStroke
    )
    drawLine(palette.primary, Offset(w * 0.42f, h * 0.74f), Offset(w * 0.58f, h * 0.74f), palette.stroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.40f, h * 0.84f), Offset(w * 0.60f, h * 0.84f), palette.thinStroke.width, StrokeCap.Round)
}

private fun DrawScope.drawTimeTravelIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawArc(
        color = palette.primary,
        startAngle = 24f,
        sweepAngle = 318f,
        useCenter = false,
        topLeft = Offset(w * 0.20f, h * 0.18f),
        size = Size(w * 0.60f, h * 0.60f),
        style = palette.stroke
    )
    drawCircle(palette.secondary, w * 0.025f, Offset(w * 0.50f, h * 0.48f))
    drawLine(palette.primary, Offset(w * 0.50f, h * 0.48f), Offset(w * 0.50f, h * 0.32f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.50f, h * 0.48f), Offset(w * 0.64f, h * 0.48f), palette.thinStroke.width, StrokeCap.Round)
    drawPath(
        path = Path().apply {
            moveTo(w * 0.73f, h * 0.22f)
            lineTo(w * 0.87f, h * 0.28f)
            lineTo(w * 0.76f, h * 0.38f)
        },
        color = palette.secondary,
        style = palette.thinStroke
    )
}

private fun DrawScope.drawCultIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(palette.secondary, w * 0.26f, Offset(w * 0.46f, h * 0.50f))
    drawCircle(MovieMateColors.CardBackground, w * 0.045f, Offset(w * 0.36f, h * 0.39f))
    drawCircle(MovieMateColors.CardBackground, w * 0.045f, Offset(w * 0.50f, h * 0.34f))
    drawCircle(MovieMateColors.CardBackground, w * 0.045f, Offset(w * 0.59f, h * 0.49f))
    drawCircle(MovieMateColors.CardBackground, w * 0.045f, Offset(w * 0.43f, h * 0.63f))
    drawCircle(palette.primary, w * 0.045f, Offset(w * 0.46f, h * 0.50f))
    drawLine(palette.primary, Offset(w * 0.62f, h * 0.71f), Offset(w * 0.82f, h * 0.71f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.72f, h * 0.69f), Offset(w * 0.78f, h * 0.64f), palette.thinStroke.width, StrokeCap.Round)
}

private fun DrawScope.drawDisasterIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.50f, h * 0.86f)
            cubicTo(w * 0.27f, h * 0.75f, w * 0.28f, h * 0.52f, w * 0.45f, h * 0.33f)
            cubicTo(w * 0.50f, h * 0.46f, w * 0.59f, h * 0.43f, w * 0.61f, h * 0.18f)
            cubicTo(w * 0.84f, h * 0.44f, w * 0.82f, h * 0.76f, w * 0.50f, h * 0.86f)
            close()
        },
        color = palette.secondary
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.50f, h * 0.78f)
            cubicTo(w * 0.39f, h * 0.67f, w * 0.44f, h * 0.55f, w * 0.55f, h * 0.44f)
            cubicTo(w * 0.63f, h * 0.58f, w * 0.65f, h * 0.72f, w * 0.50f, h * 0.78f)
            close()
        },
        color = palette.primary
    )
}

private fun DrawScope.drawSurvivalIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(palette.primary, w * 0.26f, Offset(w * 0.50f, h * 0.50f), style = palette.stroke)
    drawLine(palette.primary, Offset(w * 0.50f, h * 0.18f), Offset(w * 0.50f, h * 0.30f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.50f, h * 0.70f), Offset(w * 0.50f, h * 0.82f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.18f, h * 0.50f), Offset(w * 0.30f, h * 0.50f), palette.thinStroke.width, StrokeCap.Round)
    drawLine(palette.primary, Offset(w * 0.70f, h * 0.50f), Offset(w * 0.82f, h * 0.50f), palette.thinStroke.width, StrokeCap.Round)
    drawCircle(palette.secondary, w * 0.09f, Offset(w * 0.50f, h * 0.50f), style = palette.thinStroke)
}

private fun DrawScope.drawOtherIcon(palette: CategoryIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(palette.primary, w * 0.045f, Offset(w * 0.32f, h * 0.50f))
    drawCircle(palette.primary, w * 0.045f, Offset(w * 0.50f, h * 0.50f))
    drawCircle(palette.primary, w * 0.045f, Offset(w * 0.68f, h * 0.50f))
}

private fun DrawScope.drawMask(
    palette: CategoryIconPalette,
    topLeft: Offset,
    width: Float,
    height: Float,
    sad: Boolean,
) {
    val mask = Path().apply {
        moveTo(topLeft.x, topLeft.y)
        quadraticTo(topLeft.x + width * 0.50f, topLeft.y - height * 0.12f, topLeft.x + width, topLeft.y)
        lineTo(topLeft.x + width, topLeft.y + height * 0.56f)
        cubicTo(
            topLeft.x + width * 0.94f,
            topLeft.y + height * 0.86f,
            topLeft.x + width * 0.62f,
            topLeft.y + height,
            topLeft.x + width * 0.50f,
            topLeft.y + height
        )
        cubicTo(
            topLeft.x + width * 0.38f,
            topLeft.y + height,
            topLeft.x + width * 0.06f,
            topLeft.y + height * 0.86f,
            topLeft.x,
            topLeft.y + height * 0.56f
        )
        close()
    }
    drawPath(mask, palette.secondary)
    drawPath(mask, palette.primary, style = palette.thinStroke)
    drawCircle(MovieMateColors.CardBackground, width * 0.055f, Offset(topLeft.x + width * 0.36f, topLeft.y + height * 0.42f))
    drawCircle(MovieMateColors.CardBackground, width * 0.055f, Offset(topLeft.x + width * 0.64f, topLeft.y + height * 0.42f))
    drawArc(
        color = MovieMateColors.CardBackground,
        startAngle = if (sad) 205f else 20f,
        sweepAngle = if (sad) 130f else 140f,
        useCenter = false,
        topLeft = Offset(topLeft.x + width * 0.33f, topLeft.y + height * if (sad) 0.60f else 0.50f),
        size = Size(width * 0.34f, height * 0.20f),
        style = palette.thinStroke
    )
}

private fun DrawScope.drawSparkle(
    color: Color,
    centerX: Float,
    centerY: Float,
    radius: Float,
    strokeWidth: Float,
) {
    drawLine(color, Offset(centerX, centerY - radius), Offset(centerX, centerY + radius), strokeWidth, StrokeCap.Round)
    drawLine(color, Offset(centerX - radius, centerY), Offset(centerX + radius, centerY), strokeWidth, StrokeCap.Round)
}
