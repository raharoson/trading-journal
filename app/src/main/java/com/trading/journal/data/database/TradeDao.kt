package com.trading.journal.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades ORDER BY entryDate DESC")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Query("SELECT * FROM trades WHERE id = :id")
    suspend fun getTradeById(id: Long): TradeEntity?

    @Query("SELECT * FROM trades WHERE exitPrice IS NULL ORDER BY entryDate DESC")
    fun getOpenTrades(): Flow<List<TradeEntity>>

    @Query("SELECT * FROM trades WHERE exitPrice IS NOT NULL ORDER BY exitDate DESC")
    fun getClosedTrades(): Flow<List<TradeEntity>>

    @Query("SELECT * FROM trades WHERE symbol LIKE '%' || :query || '%' ORDER BY entryDate DESC")
    fun searchTrades(query: String): Flow<List<TradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeEntity): Long

    @Update
    suspend fun updateTrade(trade: TradeEntity)

    @Delete
    suspend fun deleteTrade(trade: TradeEntity)

    @Query("DELETE FROM trades WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM trades")
    suspend fun getTradeCount(): Int
}
