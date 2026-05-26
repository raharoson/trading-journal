package com.trading.journal.util

import android.graphics.*

object ChartBitmapRenderer {

    private val BG_COLOR = Color.parseColor("#0A0E17")
    private val CARD_COLOR = Color.parseColor("#131929")
    private val PROFIT_GREEN = Color.parseColor("#00E676")
    private val LOSS_RED = Color.parseColor("#FF5252")
    private val ON_BACKGROUND = Color.parseColor("#E8EAF0")
    private val ON_SURFACE_VARIANT = Color.parseColor("#78909C")
    private val PRIMARY = Color.parseColor("#4FC3F7")

    fun renderEquityChart(
        equityCurve: List<Double>,
        totalPnl: Double,
        maxDrawdown: Double,
        closedTrades: Int,
        widthPx: Int = 1080,
    ): Bitmap {
        val headerH = 80f
        val chartH = 240f
        val statsH = 80f
        val footerH = 48f
        val totalH = (headerH + chartH + statsH + footerH).toInt()

        val bitmap = Bitmap.createBitmap(widthPx, totalH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(CARD_COLOR)

        // ── Header ────────────────────────────────────────────────
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ON_BACKGROUND
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Courbe d'équité", 32f, headerH - 24f, titlePaint)

        // ── Equity curve ──────────────────────────────────────────
        drawChart(canvas, equityCurve, 32f, headerH, widthPx - 64f, chartH)

        // ── Stats row ─────────────────────────────────────────────
        val statsY = headerH + chartH + 20f
        val pnlColor = if (totalPnl >= 0) PROFIT_GREEN else LOSS_RED
        val pnlSign = if (totalPnl >= 0) "+" else ""
        drawStat(canvas, widthPx / 6f, statsY, "P&L Total", "${pnlSign}${"%.2f".format(totalPnl)}€", pnlColor)
        drawStat(canvas, widthPx / 2f, statsY, "Drawdown max", "-${"%.2f".format(maxDrawdown)}€", LOSS_RED)
        drawStat(canvas, widthPx * 5 / 6f, statsY, "Trades clôt.", "$closedTrades", ON_BACKGROUND)

        // ── Footer watermark ──────────────────────────────────────
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = PRIMARY.withAlpha(120)
            textSize = 20f
        }
        val footerText = "Trading Journal"
        val footerX = widthPx - footerPaint.measureText(footerText) - 24f
        canvas.drawText(footerText, footerX, totalH - 14f, footerPaint)

        return bitmap
    }

    private fun drawChart(
        canvas: Canvas,
        data: List<Double>,
        left: Float,
        top: Float,
        width: Float,
        height: Float
    ) {
        if (data.size < 2) return

        val isPositive = data.last() >= data.first()
        val lineColorInt = if (isPositive) PROFIT_GREEN else LOSS_RED

        val maxVal = data.max()
        val minVal = data.min()
        val range = if (maxVal == minVal) 1.0 else maxVal - minVal

        val padTop = 16f
        val padBottom = 16f
        val chartHeight = height - padTop - padBottom
        val stepX = width / (data.size - 1).coerceAtLeast(1)

        fun yFor(v: Double): Float =
            (top + padTop + chartHeight * (1.0 - (v - minVal) / range)).toFloat()

        val points = data.mapIndexed { i, v -> PointF(left + i * stepX, yFor(v)) }

        // Grid lines
        val gridPaint = Paint().apply {
            color = Color.argb(18, 255, 255, 255)
            strokeWidth = 1.5f
        }
        repeat(5) { i ->
            val y = top + padTop + chartHeight * i / 4
            canvas.drawLine(left, y, left + width, y, gridPaint)
        }

        // Zero line
        if (minVal < 0 && maxVal > 0) {
            val zeroY = yFor(0.0)
            val zeroPaint = Paint().apply {
                color = Color.argb(60, 255, 255, 255)
                strokeWidth = 1.5f
                pathEffect = DashPathEffect(floatArrayOf(12f, 8f), 0f)
            }
            canvas.drawLine(left, zeroY, left + width, zeroY, zeroPaint)
        }

        // Fill gradient
        val baselineY = yFor(minVal.coerceAtMost(0.0))
        val fillPath = Path().apply {
            moveTo(left, baselineY)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, baselineY)
            close()
        }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, top + padTop, 0f, top + height,
                lineColorInt.withAlpha(90),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(fillPath, fillPaint)

        // Bezier curve
        val linePath = Path()
        linePath.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]
            val cpX = (prev.x + curr.x) / 2f
            linePath.cubicTo(cpX, prev.y, cpX, curr.y, curr.x, curr.y)
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = lineColorInt
            strokeWidth = 4f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        canvas.drawPath(linePath, linePaint)

        // End dot
        val last = points.last()
        canvas.drawCircle(last.x, last.y, 8f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = lineColorInt })
        canvas.drawCircle(last.x, last.y, 4f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
    }

    private fun drawStat(
        canvas: Canvas,
        centerX: Float,
        topY: Float,
        label: String,
        value: String,
        valueColor: Int
    ) {
        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = valueColor
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ON_SURFACE_VARIANT
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(value, centerX, topY + 32f, valuePaint)
        canvas.drawText(label, centerX, topY + 58f, labelPaint)
    }

    private fun Int.withAlpha(alpha: Int): Int =
        Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
}
