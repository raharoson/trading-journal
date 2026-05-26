package com.trading.journal.ui.statistics;

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
public final class StatisticsViewModel_Factory implements Factory<StatisticsViewModel> {
  private final Provider<TradeRepository> repositoryProvider;

  public StatisticsViewModel_Factory(Provider<TradeRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public StatisticsViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static StatisticsViewModel_Factory create(Provider<TradeRepository> repositoryProvider) {
    return new StatisticsViewModel_Factory(repositoryProvider);
  }

  public static StatisticsViewModel newInstance(TradeRepository repository) {
    return new StatisticsViewModel(repository);
  }
}
