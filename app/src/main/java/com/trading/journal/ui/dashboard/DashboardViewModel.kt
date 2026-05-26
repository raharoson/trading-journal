package com.trading.journal.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.journal.data.repository.TradeRepository
import com.trading.journal.domain.model.Trade
import com.trading.journal.domain.model.TradeStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DashboardUiState(
    val recentTrades: List<Trade> = emptyList(),
    val statistics: TradeStatistics = TradeStatistics(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: TradeRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = repository.getAllTrades()
        .map { trades ->
            DashboardUiState(
                recentTrades = trades.take(5),
                statistics = TradeStatistics.from(trades),
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )
}
