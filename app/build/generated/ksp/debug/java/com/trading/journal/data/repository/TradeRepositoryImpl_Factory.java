package com.trading.journal.data.repository;

import com.trading.journal.data.database.TradeDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class TradeRepositoryImpl_Factory implements Factory<TradeRepositoryImpl> {
  private final Provider<TradeDao> daoProvider;

  public TradeRepositoryImpl_Factory(Provider<TradeDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public TradeRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static TradeRepositoryImpl_Factory create(Provider<TradeDao> daoProvider) {
    return new TradeRepositoryImpl_Factory(daoProvider);
  }

  public static TradeRepositoryImpl newInstance(TradeDao dao) {
    return new TradeRepositoryImpl(dao);
  }
}
