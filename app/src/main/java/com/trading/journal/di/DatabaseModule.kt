package com.trading.journal.di

import android.content.Context
import androidx.room.Room
import com.trading.journal.data.database.AppDatabase
import com.trading.journal.data.database.TradeDao
import com.trading.journal.data.repository.TradeRepository
import com.trading.journal.data.repository.TradeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTradeDao(db: AppDatabase): TradeDao = db.tradeDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTradeRepository(impl: TradeRepositoryImpl): TradeRepository
}
