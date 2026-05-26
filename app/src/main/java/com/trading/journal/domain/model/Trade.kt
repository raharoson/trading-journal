package com.trading.journal.domain.model

import java.time.LocalDateTime
import kotlin.math.abs

data class Trade(
    val id: Long = 0,
    val symbol: String,
    val direction: TradeDirection,
    val market: TradeMarket,
    val entryPrice: Double,
    val exitPrice: Double?,
    val quantity: Double,
    val entryDate: LocalDateTime,
    val exitDate: LocalDateTime?,
    val fees: Double = 0.0,
    val stopLoss: Double? = null,
    val takeProfit: Double? = null,
    val tags: List<String> = emptyList(),
    val notes: String = ""
) {
    val pnl: Double?
        get() = exitPrice?.let { exit ->
            val gross = when (direction) {
                TradeDirection.LONG -> (exit - entryPrice) * quantity
                TradeDirection.SHORT -> (entryPrice - exit) * quantity
            }
            gross - fees
        }

    val pnlPercent: Double?
        get() = exitPrice?.let { exit ->
            val diff = when (direction) {
                TradeDirection.LONG -> exit - entryPrice
                TradeDirection.SHORT -> entryPrice - exit
            }
            (diff / entryPrice) * 100.0
        }

    val isOpen: Boolean get() = exitPrice == null

    val isWin: Boolean? get() = pnl?.let { it > 0 }

    val rMultiple: Double?
        get() {
            val sl = stopLoss ?: return null
            val p = pnl ?: return null
            val risk = abs(entryPrice - sl) * quantity
            return if (risk == 0.0) null else p / risk
        }

    val notionalValue: Double get() = entryPrice * quantity
}
