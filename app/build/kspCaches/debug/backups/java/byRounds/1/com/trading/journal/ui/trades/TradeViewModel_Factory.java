package com.trading.journal.ui.trades;

import com.trading.journal.data.repository.TradeRepository;
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
public final class TradeViewModel_Factory implements Factory<TradeViewModel> {
  private final Provider<TradeRepository> repositoryProvider;

  public TradeViewModel_Factory(Provider<TradeRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public TradeViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static TradeViewModel_Factory create(Provider<TradeRepository> repositoryProvider) {
    return new TradeViewModel_Factory(repositoryProvider);
  }

  public static TradeViewModel newInstance(TradeRepository repository) {
    return new TradeViewModel(repository);
  }
}
