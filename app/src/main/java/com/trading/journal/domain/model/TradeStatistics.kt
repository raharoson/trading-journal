package com.trading.journal.domain.model

data class TradeStatistics(
    val totalTrades: Int = 0,
    val openTrades: Int = 0,
    val closedTrades: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val winRate: Float = 0f,
    val totalPnl: Double = 0.0,
    val avgPnl: Double = 0.0,
    val bestTrade: Double = 0.0,
    val worstTrade: Double = 0.0,
    val avgWin: Double = 0.0,
    val avgLoss: Double = 0.0,
    val profitFactor: Double = 0.0,
    val maxDrawdown: Double = 0.0,
    val equityCurve: List<Double> = emptyList(),
    val pnlBySymbol: Map<String, Double> = emptyMap()
) {
    companion object {
        fun from(trades: List<Trade>): TradeStatistics {
            val closed = trades.filter { !it.isOpen }
            if (closed.isEmpty()) return TradeStatistics(
                totalTrades = trades.size,
                openTrades = trades.count { it.isOpen }
            )

            val pnls = closed.mapNotNull { it.pnl }
            val wins = pnls.filter { it > 0 }
            val losses = pnls.filter { it < 0 }

            val grossProfit = wins.sum()
            val grossLoss = losses.sumOf { kotlin.math.abs(it) }

            val equityCurve = buildEquityCurve(closed)
            val maxDrawdown = computeMaxDrawdown(equityCurve)

            val pnlBySymbol = closed
                .groupBy { it.symbol }
                .mapValues { (_, ts) -> ts.mapNotNull { it.pnl }.sum() }

            return TradeStatistics(
                totalTrades = trades.size,
                openTrades = trades.count { it.isOpen },
                closedTrades = closed.size,
                wins = wins.size,
                losses = losses.size,
                winRate = if (pnls.isEmpty()) 0f else wins.size.toFloat() / pnls.size,
                totalPnl = pnls.sum(),
                avgPnl = if (pnls.isEmpty()) 0.0 else pnls.average(),
                bestTrade = if (wins.isEmpty()) 0.0 else wins.max(),
                worstTrade = if (losses.isEmpty()) 0.0 else losses.min(),
                avgWin = if (wins.isEmpty()) 0.0 else wins.average(),
                avgLoss = if (losses.isEmpty()) 0.0 else losses.average(),
                profitFactor = if (grossLoss == 0.0) grossProfit else grossProfit / grossLoss,
                maxDrawdown = maxDrawdown,
                equityCurve = equityCurve,
                pnlBySymbol = pnlBySymbol
            )
        }

        private fun buildEquityCurve(trades: List<Trade>): List<Double> {
            val sorted = trades.sortedBy { it.exitDate }
            var running = 0.0
            return listOf(0.0) + sorted.mapNotNull { it.pnl }.map { pnl ->
                running += pnl
                running
            }
        }

        private fun computeMaxDrawdown(curve: List<Double>): Double {
            var peak = Double.MIN_VALUE
            var maxDd = 0.0
            for (value in curve) {
                if (value > peak) peak = value
                val dd = peak - value
                if (dd > maxDd) maxDd = dd
            }
            return maxDd
        }
    }
}
