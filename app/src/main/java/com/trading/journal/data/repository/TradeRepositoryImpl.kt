package com.trading.journal.data.repository

import com.trading.journal.data.database.TradeDao
import com.trading.journal.data.database.TradeEntity
import com.trading.journal.domain.model.Trade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TradeRepositoryImpl @Inject constructor(
    private val dao: TradeDao
) : TradeRepository {

    override fun getAllTrades(): Flow<List<Trade>> =
        dao.getAllTrades().map { entities -> entities.map { it.toDomain() } }

    override fun getOpenTrades(): Flow<List<Trade>> =
        dao.getOpenTrades().map { entities -> entities.map { it.toDomain() } }

    override fun getClosedTrades(): Flow<List<Trade>> =
        dao.getClosedTrades().map { entities -> entities.map { it.toDomain() } }

    override fun searchTrades(query: String): Flow<List<Trade>> =
        dao.searchTrades(query).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTradeById(id: Long): Trade? =
        dao.getTradeById(id)?.toDomain()

    override suspend fun saveTrade(trade: Trade): Long =
        dao.insertTrade(TradeEntity.fromDomain(trade))

    override suspend fun updateTrade(trade: Trade) =
        dao.updateTrade(TradeEntity.fromDomain(trade))

    override suspend fun deleteTrade(id: Long) =
        dao.deleteById(id)
}
