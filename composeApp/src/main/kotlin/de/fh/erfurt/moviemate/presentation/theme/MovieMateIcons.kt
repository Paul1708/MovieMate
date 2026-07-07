package de.fh.erfurt.moviemate.presentation.theme

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

object MovieMateIcons {
    const val add = "+"
    val exportBackup = MovieMateIconType.ExportBackup
    val importBackup = MovieMateIconType.ImportBackup
    val chevronRight = MovieMateIconType.ChevronRight
    val back = MovieMateIconType.Back
    val search = MovieMateIconType.Search
    val filter = MovieMateIconType.Filter
    const val more = "⋮"
    val remove = MovieMateIconType.Remove

    const val rating = "★"
    const val ratingEmpty = "☆"
    val watched = MovieMateIconType.Watched
    const val provider = "▶"
    const val updated = "↻"
    val image = MovieMateIconType.Image
}

enum class MovieMateIconType {
    Search,
    Filter,
    ExportBackup,
    ImportBackup,
    Remove,
    ChevronRight,
    Back,
    Watched,
    Image,
}

@Composable
fun MovieMateIcon(
    icon: MovieMateIconType,
    tint: Color,
    modifier: Modifier = Modifier,
    secondaryTint: Color = tint,
) {
    Canvas(modifier = modifier) {
        val palette = MovieMateIconPalette(
            primary = tint,
            secondary = secondaryTint,
            stroke = Stroke(
                width = size.minDimension * IconStrokeScale,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            ),
            thinStroke = Stroke(
                width = size.minDimension * ThinIconStrokeScale,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        when (icon) {
            MovieMateIconType.Search -> drawSearchIcon(palette)
            MovieMateIconType.Filter -> drawFilterIcon(palette)
            MovieMateIconType.ExportBackup -> drawExportBackupIcon(palette)
            MovieMateIconType.ImportBackup -> drawImportBackupIcon(palette)
            MovieMateIconType.Remove -> drawRemoveIcon(palette)
            MovieMateIconType.ChevronRight -> drawChevronRightIcon(palette)
            MovieMateIconType.Back -> drawBackIcon(palette)
            MovieMateIconType.Watched -> drawWatchedIcon(palette)
            MovieMateIconType.Image -> drawImageIcon(palette)
        }
    }
}

private data class MovieMateIconPalette(
    val primary: Color,
    val secondary: Color,
    val stroke: Stroke,
    val thinStroke: Stroke,
)

private fun DrawScope.drawSearchIcon(palette: MovieMateIconPalette) {
    val w = size.width
    val h = size.height
    drawCircle(
        color = palette.primary,
        radius = w * 0.24f,
        center = Offset(w * 0.42f, h * 0.42f),
        style = palette.stroke
    )
    drawLine(
        color = palette.secondary,
        start = Offset(w * 0.60f, h * 0.60f),
        end = Offset(w * 0.80f, h * 0.80f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawFilterIcon(palette: MovieMateIconPalette) {
    val w = size.width
    val h = size.height
    val funnel = Path().apply {
        moveTo(w * 0.18f, h * 0.24f)
        lineTo(w * 0.82f, h * 0.24f)
        lineTo(w * 0.58f, h * 0.52f)
        lineTo(w * 0.58f, h * 0.74f)
        lineTo(w * 0.42f, h * 0.80f)
        lineTo(w * 0.42f, h * 0.52f)
        close()
    }

    drawPath(
        path = funnel,
        color = palette.primary,
        style = palette.stroke
    )
    drawLine(
        color = palette.secondary,
        start = Offset(w * 0.36f, h * 0.42f),
        end = Offset(w * 0.64f, h * 0.42f),
        strokeWidth = palette.thinStroke.width,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawExportBackupIcon(palette: MovieMateIconPalette) {
    val w = size.width
    val h = size.height
    drawBackupTray(palette, top = h * 0.58f)
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.50f, h * 0.66f),
        end = Offset(w * 0.50f, h * 0.22f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.36f, h * 0.36f),
        end = Offset(w * 0.50f, h * 0.22f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.64f, h * 0.36f),
        end = Offset(w * 0.50f, h * 0.22f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawImportBackupIcon(palette: MovieMateIconPalette) {
    val w = size.width
    val h = size.height
    drawBackupTray(palette, top = h * 0.62f)
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.50f, h * 0.20f),
        end = Offset(w * 0.50f, h * 0.62f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.36f, h * 0.48f),
        end = Offset(w * 0.50f, h * 0.62f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.64f, h * 0.48f),
        end = Offset(w * 0.50f, h * 0.62f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawBackupTray(
    palette: MovieMateIconPalette,
    top: Float,
) {
    val w = size.width
    val h = size.height
    val tray = Path().apply {
        moveTo(w * 0.22f, top)
        lineTo(w * 0.22f, h * 0.80f)
        lineTo(w * 0.78f, h * 0.80f)
        lineTo(w * 0.78f, top)
    }

    drawPath(
        path = tray,
        color = palette.secondary,
        style = palette.thinStroke
    )
}

private fun DrawScope.drawRemoveIcon(palette: MovieMateIconPalette) {
    val w = size.width
    val h = size.height
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.28f, h * 0.28f),
        end = Offset(w * 0.72f, h * 0.72f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.primary,
        start = Offset(w * 0.72f, h * 0.28f),
        end = Offset(w * 0.28f, h * 0.72f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawChevronRightIcon(palette: MovieMateIconPalette) {
    val w = size.width
    val h = size.height
    val chevron = Path().apply {
        moveTo(w * 0.38f, h * 0.24f)
        lineTo(w * 0.62f, h * 0.50f)
        lineTo(w * 0.38f, h * 0.76f)
    }

    drawPath(
        path = chevron,
        color = palette.primary,
        style = palette.stroke
    )
}

private fun DrawScope.drawBackIcon(palette: MovieMateIconPalette) {
    val w = size.width
    val h = size.height
    val chevron = Path().apply {
        moveTo(w * 0.62f, h * 0.24f)
        lineTo(w * 0.38f, h * 0.50f)
        lineTo(w * 0.62f, h * 0.76f)
    }

    drawPath(
        path = chevron,
        color = palette.primary,
        style = palette.stroke
    )
}

private fun DrawScope.drawWatchedIcon(palette: MovieMateIconPalette) {
    val w = size.width
    val h = size.height
    val check = Path().apply {
        moveTo(w * 0.22f, h * 0.52f)
        lineTo(w * 0.42f, h * 0.70f)
        lineTo(w * 0.78f, h * 0.30f)
    }

    drawPath(
        path = check,
        color = palette.primary,
        style = palette.stroke
    )
}

private fun DrawScope.drawImageIcon(palette: MovieMateIconPalette) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = palette.primary,
        topLeft = Offset(w * 0.16f, h * 0.20f),
        size = Size(w * 0.68f, h * 0.60f),
        cornerRadius = CornerRadius(w * 0.08f, h * 0.08f),
        style = palette.thinStroke
    )
    drawCircle(
        color = palette.secondary,
        radius = w * 0.055f,
        center = Offset(w * 0.62f, h * 0.36f)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.22f, h * 0.72f)
            lineTo(w * 0.40f, h * 0.54f)
            lineTo(w * 0.52f, h * 0.66f)
            lineTo(w * 0.62f, h * 0.56f)
            lineTo(w * 0.78f, h * 0.72f)
        },
        color = palette.primary,
        style = palette.thinStroke
    )
}

private const val IconStrokeScale = 0.095f
private const val ThinIconStrokeScale = 0.070f
