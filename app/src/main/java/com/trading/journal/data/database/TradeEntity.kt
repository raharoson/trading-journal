package com.trading.journal.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trading.journal.domain.model.Trade
import com.trading.journal.domain.model.TradeDirection
import com.trading.journal.domain.model.TradeMarket
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val direction: String,
    val market: String,
    val entryPrice: Double,
    val exitPrice: Double?,
    val quantity: Double,
    val entryDate: Long,
    val exitDate: Long?,
    val fees: Double,
    val stopLoss: Double?,
    val takeProfit: Double?,
    val tags: String,
    val notes: String
) {
    fun toDomain(): Trade = Trade(
        id = id,
        symbol = symbol,
        direction = TradeDirection.valueOf(direction),
        market = TradeMarket.valueOf(market),
        entryPrice = entryPrice,
        exitPrice = exitPrice,
        quantity = quantity,
        entryDate = epochToLocalDateTime(entryDate),
        exitDate = exitDate?.let { epochToLocalDateTime(it) },
        fees = fees,
        stopLoss = stopLoss,
        takeProfit = takeProfit,
        tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
        notes = notes
    )

    companion object {
        fun fromDomain(trade: Trade): TradeEntity = TradeEntity(
            id = trade.id,
            symbol = trade.symbol.uppercase().trim(),
            direction = trade.direction.name,
            market = trade.market.name,
            entryPrice = trade.entryPrice,
            exitPrice = trade.exitPrice,
            quantity = trade.quantity,
            entryDate = localDateTimeToEpoch(trade.entryDate),
            exitDate = trade.exitDate?.let { localDateTimeToEpoch(it) },
            fees = trade.fees,
            stopLoss = trade.stopLoss,
            takeProfit = trade.takeProfit,
            tags = trade.tags.joinToString(","),
            notes = trade.notes
        )

        private fun epochToLocalDateTime(epoch: Long): LocalDateTime =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault())

        private fun localDateTimeToEpoch(dt: LocalDateTime): Long =
            dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
