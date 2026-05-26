package com.trading.journal

import com.trading.journal.domain.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class TradeStatisticsTest {

    private fun trade(
        symbol: String = "AAPL",
        direction: TradeDirection = TradeDirection.LONG,
        entry: Double = 100.0,
        exit: Double? = 110.0,
        qty: Double = 10.0,
        fees: Double = 1.0
    ) = Trade(
        symbol = symbol,
        direction = direction,
        market = TradeMarket.STOCKS,
        entryPrice = entry,
        exitPrice = exit,
        quantity = qty,
        entryDate = LocalDateTime.now(),
        exitDate = if (exit != null) LocalDateTime.now() else null,
        fees = fees
    )

    @Test
    fun `pnl long trade positive`() {
        val t = trade(entry = 100.0, exit = 110.0, qty = 10.0, fees = 1.0)
        assertEquals(99.0, t.pnl!!, 0.001)
    }

    @Test
    fun `pnl short trade positive`() {
        val t = trade(direction = TradeDirection.SHORT, entry = 110.0, exit = 100.0, qty = 10.0, fees = 1.0)
        assertEquals(99.0, t.pnl!!, 0.001)
    }

    @Test
    fun `open trade has null pnl`() {
        val t = trade(exit = null)
        assertNull(t.pnl)
        assertTrue(t.isOpen)
    }

    @Test
    fun `statistics win rate calculation`() {
        val trades = listOf(
            trade(exit = 110.0), // win
            trade(exit = 90.0),  // loss
            trade(exit = 115.0)  // win
        )
        val stats = TradeStatistics.from(trades)
        assertEquals(2, stats.wins)
        assertEquals(1, stats.losses)
        assertEquals(2f / 3f, stats.winRate, 0.001f)
    }

    @Test
    fun `empty trades returns default statistics`() {
        val stats = TradeStatistics.from(emptyList())
        assertEquals(0, stats.totalTrades)
        assertEquals(0f, stats.winRate, 0.001f)
    }
}
