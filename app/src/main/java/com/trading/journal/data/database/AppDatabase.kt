package com.trading.journal.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TradeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao

    companion object {
        const val DATABASE_NAME = "trading_journal.db"
    }
}
