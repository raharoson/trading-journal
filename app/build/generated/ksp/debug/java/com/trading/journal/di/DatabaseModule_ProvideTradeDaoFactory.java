package com.trading.journal.di;

import com.trading.journal.data.database.AppDatabase;
import com.trading.journal.data.database.TradeDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DatabaseModule_ProvideTradeDaoFactory implements Factory<TradeDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideTradeDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TradeDao get() {
    return provideTradeDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideTradeDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideTradeDaoFactory(dbProvider);
  }

  public static TradeDao provideTradeDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTradeDao(db));
  }
}
