package xyz.larkzhh.echomusic.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import kotlin.math.min


@Composable
fun CircleProgress(modifier: Modifier = Modifier, progress: Int) {
    val sweepAngle = progress / 100f * 360
    val progressColor = AppColorsProvider.current.primary
    Canvas(modifier = modifier) {
        val canvasSize = min(size.width, size.height)
        drawCircle(color = Color.LightGray, radius = canvasSize / 2, style = Stroke(width = 4f))
        drawArc(color = progressColor, style = Stroke(width = 4f), startAngle = -90f, sweepAngle = sweepAngle, useCenter = false)
    }
}
