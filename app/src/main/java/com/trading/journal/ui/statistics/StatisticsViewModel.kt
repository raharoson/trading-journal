package com.trading.journal.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.journal.data.repository.TradeRepository
import com.trading.journal.domain.model.TradeStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StatisticsUiState(
    val statistics: TradeStatistics = TradeStatistics(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    repository: TradeRepository
) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> = repository.getAllTrades()
        .map { trades ->
            StatisticsUiState(
                statistics = TradeStatistics.from(trades),
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatisticsUiState()
        )
}
