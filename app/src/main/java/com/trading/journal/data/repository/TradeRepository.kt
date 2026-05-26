package com.trading.journal.data.repository

import com.trading.journal.domain.model.Trade
import kotlinx.coroutines.flow.Flow

interface TradeRepository {
    fun getAllTrades(): Flow<List<Trade>>
    fun getOpenTrades(): Flow<List<Trade>>
    fun getClosedTrades(): Flow<List<Trade>>
    fun searchTrades(query: String): Flow<List<Trade>>
    suspend fun getTradeById(id: Long): Trade?
    suspend fun saveTrade(trade: Trade): Long
    suspend fun updateTrade(trade: Trade)
    suspend fun deleteTrade(id: Long)
}
