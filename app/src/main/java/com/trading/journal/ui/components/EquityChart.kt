package com.trading.journal.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trading.journal.ui.theme.*

@Composable
fun EquityChart(
    data: List<Double>,
    modifier: Modifier = Modifier
) {
    if (data.size < 2) {
        Box(
            modifier = modifier
                .background(CardBackground, RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Aucune donnée",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
        }
        return
    }

    val lastValue = data.last()
    val firstValue = data.first()
    val isPositive = lastValue >= firstValue
    val lineColor = if (isPositive) ProfitGreen else LossRed

    Canvas(modifier = modifier.fillMaxWidth().height(180.dp)) {
        val maxVal = data.max()
        val minVal = data.min()
        val range = if (maxVal == minVal) 1.0 else maxVal - minVal

        val padTop = 20f
        val padBottom = 20f
        val chartHeight = size.height - padTop - padBottom
        val stepX = size.width / (data.size - 1).coerceAtLeast(1)

        fun yFor(v: Double): Float =
            (padTop + chartHeight * (1.0 - (v - minVal) / range)).toFloat()

        val points = data.mapIndexed { i, v -> Offset(i * stepX, yFor(v)) }

        // Grid lines (horizontal)
        val gridColor = Color.White.copy(alpha = 0.06f)
        repeat(5) { i ->
            val y = padTop + chartHeight * i / 4
            drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        }

        // Zero line if in range
        if (minVal < 0 && maxVal > 0) {
            val zeroY = yFor(0.0)
            drawLine(
                Color.White.copy(alpha = 0.2f),
                Offset(0f, zeroY),
                Offset(size.width, zeroY),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
            )
        }

        // Fill area
        val fillPath = Path().apply {
            val baseline = yFor(minVal.coerceAtMost(0.0))
            moveTo(0f, baseline)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, baseline)
            close()
        }
        drawPath(
            fillPath,
            brush = Brush.verticalGradient(
                0f to lineColor.copy(alpha = 0.35f),
                1f to lineColor.copy(alpha = 0.0f)
            )
        )

        // Smooth line (cubic bezier)
        val linePath = Path()
        linePath.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]
            val cpX = (prev.x + curr.x) / 2f
            linePath.cubicTo(cpX, prev.y, cpX, curr.y, curr.x, curr.y)
        }
        drawPath(
            linePath,
            color = lineColor,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Last point dot
        val last = points.last()
        drawCircle(lineColor, radius = 5f, center = last)
        drawCircle(Color.White, radius = 2.5f, center = last)
    }
}

@Composable
fun PnlBarChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    val maxAbs = data.values.maxOf { kotlin.math.abs(it) }.coerceAtLeast(0.01)

    Column(modifier = modifier.fillMaxWidth()) {
        data.entries.sortedByDescending { it.value }.take(8).forEach { (symbol, pnl) ->
            val fraction = (kotlin.math.abs(pnl) / maxAbs).toFloat()
            val color = if (pnl >= 0) ProfitGreen else LossRed
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = OnBackground,
                    modifier = Modifier.width(72.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .background(CardBorder, RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .background(color.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${if (pnl >= 0) "+" else ""}${"%.0f".format(pnl)}€",
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.width(70.dp),
                )
            }
        }
    }
}
