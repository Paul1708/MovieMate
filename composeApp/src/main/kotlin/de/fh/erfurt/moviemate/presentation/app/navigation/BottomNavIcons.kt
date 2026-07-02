package de.fh.erfurt.moviemate.presentation.app.navigation

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

enum class BottomNavIconType {
    Home,
    Categories,
    Add,
    Backup,
}

@Composable
internal fun BottomNavIcon(
    icon: BottomNavIconType,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val palette = BottomNavIconPalette(
            color = color,
            stroke = Stroke(
                width = size.minDimension * 0.072f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            ),
            thinStroke = Stroke(
                width = size.minDimension * 0.058f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        when (icon) {
            BottomNavIconType.Home -> drawHomeIcon(palette)
            BottomNavIconType.Categories -> drawCategoriesIcon(palette)
            BottomNavIconType.Add -> drawAddIcon(palette)
            BottomNavIconType.Backup -> drawBackupIcon(palette)
        }
    }
}

private data class BottomNavIconPalette(
    val color: Color,
    val stroke: Stroke,
    val thinStroke: Stroke,
)

private fun DrawScope.drawHomeIcon(palette: BottomNavIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.18f, h * 0.48f)
            lineTo(w * 0.50f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.48f)
            lineTo(w * 0.82f, h * 0.82f)
            lineTo(w * 0.60f, h * 0.82f)
            lineTo(w * 0.60f, h * 0.62f)
            lineTo(w * 0.40f, h * 0.62f)
            lineTo(w * 0.40f, h * 0.82f)
            lineTo(w * 0.18f, h * 0.82f)
            close()
        },
        color = palette.color,
        style = palette.stroke
    )
}

private fun DrawScope.drawCategoriesIcon(palette: BottomNavIconPalette) {
    val w = size.width
    val h = size.height
    val tile = Size(w * 0.19f, h * 0.19f)
    val radius = CornerRadius(w * 0.035f, h * 0.035f)
    listOf(
        Offset(w * 0.23f, h * 0.22f),
        Offset(w * 0.58f, h * 0.22f),
        Offset(w * 0.23f, h * 0.58f),
        Offset(w * 0.58f, h * 0.58f)
    ).forEach { topLeft ->
        drawRoundRect(
            color = palette.color,
            topLeft = topLeft,
            size = tile,
            cornerRadius = radius,
            style = palette.stroke
        )
    }
}

private fun DrawScope.drawAddIcon(palette: BottomNavIconPalette) {
    val w = size.width
    val h = size.height
    drawLine(
        color = palette.color,
        start = Offset(w * 0.50f, h * 0.18f),
        end = Offset(w * 0.50f, h * 0.82f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
    drawLine(
        color = palette.color,
        start = Offset(w * 0.18f, h * 0.50f),
        end = Offset(w * 0.82f, h * 0.50f),
        strokeWidth = palette.stroke.width,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawBackupIcon(palette: BottomNavIconPalette) {
    val w = size.width
    val h = size.height
    drawPath(
        path = Path().apply {
            moveTo(w * 0.18f, h * 0.34f)
            lineTo(w * 0.38f, h * 0.34f)
            lineTo(w * 0.46f, h * 0.44f)
            lineTo(w * 0.82f, h * 0.44f)
            lineTo(w * 0.82f, h * 0.78f)
            lineTo(w * 0.18f, h * 0.78f)
            close()
        },
        color = palette.color,
        style = palette.stroke
    )
}
