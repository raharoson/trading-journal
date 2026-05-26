package com.trading.journal.ui.trades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.journal.data.repository.TradeRepository
import com.trading.journal.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

enum class TradeFilter { ALL, OPEN, CLOSED, LONG, SHORT }
enum class SortOption { DATE_DESC, DATE_ASC, PNL_DESC, PNL_ASC, SYMBOL }

data class TradeListUiState(
    val trades: List<Trade> = emptyList(),
    val filter: TradeFilter = TradeFilter.ALL,
    val sort: SortOption = SortOption.DATE_DESC,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

data class AddEditUiState(
    val id: Long = 0,
    val symbol: String = "",
    val direction: TradeDirection = TradeDirection.LONG,
    val market: TradeMarket = TradeMarket.STOCKS,
    val entryPrice: String = "",
    val exitPrice: String = "",
    val quantity: String = "",
    val fees: String = "0",
    val stopLoss: String = "",
    val takeProfit: String = "",
    val entryDate: LocalDateTime = LocalDateTime.now(),
    val exitDate: LocalDateTime? = null,
    val tags: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val repository: TradeRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(TradeFilter.ALL)
    private val _sort = MutableStateFlow(SortOption.DATE_DESC)
    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val listUiState: StateFlow<TradeListUiState> = combine(
        repository.getAllTrades(),
        _filter,
        _sort,
        _searchQuery
    ) { trades, filter, sort, query ->
        val filtered = trades
            .filter { trade ->
                val matchesFilter = when (filter) {
                    TradeFilter.ALL -> true
                    TradeFilter.OPEN -> trade.isOpen
                    TradeFilter.CLOSED -> !trade.isOpen
                    TradeFilter.LONG -> trade.direction == TradeDirection.LONG
                    TradeFilter.SHORT -> trade.direction == TradeDirection.SHORT
                }
                val matchesSearch = query.isBlank() ||
                        trade.symbol.contains(query, ignoreCase = true) ||
                        trade.tags.any { it.contains(query, ignoreCase = true) }
                matchesFilter && matchesSearch
            }
            .let { list ->
                when (sort) {
                    SortOption.DATE_DESC -> list.sortedByDescending { it.entryDate }
                    SortOption.DATE_ASC -> list.sortedBy { it.entryDate }
                    SortOption.PNL_DESC -> list.sortedByDescending { it.pnl ?: Double.MIN_VALUE }
                    SortOption.PNL_ASC -> list.sortedBy { it.pnl ?: Double.MAX_VALUE }
                    SortOption.SYMBOL -> list.sortedBy { it.symbol }
                }
            }
        TradeListUiState(
            trades = filtered,
            filter = filter,
            sort = sort,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        TradeListUiState()
    )

    private val _addEditState = MutableStateFlow(AddEditUiState())
    val addEditState: StateFlow<AddEditUiState> = _addEditState.asStateFlow()

    fun setFilter(filter: TradeFilter) { _filter.value = filter }
    fun setSort(sort: SortOption) { _sort.value = sort }
    fun setSearchQuery(q: String) { _searchQuery.value = q }

    fun loadTradeForEdit(id: Long) {
        viewModelScope.launch {
            _addEditState.value = _addEditState.value.copy(isLoading = true)
            val trade = repository.getTradeById(id)
            if (trade != null) {
                _addEditState.value = AddEditUiState(
                    id = trade.id,
                    symbol = trade.symbol,
                    direction = trade.direction,
                    market = trade.market,
                    entryPrice = trade.entryPrice.toString(),
                    exitPrice = trade.exitPrice?.toString() ?: "",
                    quantity = trade.quantity.toString(),
                    fees = trade.fees.toString(),
                    stopLoss = trade.stopLoss?.toString() ?: "",
                    takeProfit = trade.takeProfit?.toString() ?: "",
                    entryDate = trade.entryDate,
                    exitDate = trade.exitDate,
                    tags = trade.tags.joinToString(", "),
                    notes = trade.notes,
                    isLoading = false
                )
            } else {
                _addEditState.value = _addEditState.value.copy(isLoading = false)
            }
        }
    }

    fun resetAddEditState() { _addEditState.value = AddEditUiState() }

    fun updateField(update: AddEditUiState.() -> AddEditUiState) {
        _addEditState.value = _addEditState.value.update()
    }

    fun saveTrade() {
        val s = _addEditState.value
        if (s.symbol.isBlank()) {
            _addEditState.value = s.copy(error = "Le symbole est requis")
            return
        }
        val entryPrice = s.entryPrice.toDoubleOrNull()
        if (entryPrice == null || entryPrice <= 0) {
            _addEditState.value = s.copy(error = "Prix d'entrée invalide")
            return
        }
        val quantity = s.quantity.toDoubleOrNull()
        if (quantity == null || quantity <= 0) {
            _addEditState.value = s.copy(error = "Quantité invalide")
            return
        }

        _addEditState.value = s.copy(isSaving = true, error = null)
        viewModelScope.launch {
            val trade = Trade(
                id = s.id,
                symbol = s.symbol.uppercase().trim(),
                direction = s.direction,
                market = s.market,
                entryPrice = entryPrice,
                exitPrice = s.exitPrice.toDoubleOrNull(),
                quantity = quantity,
                entryDate = s.entryDate,
                exitDate = s.exitDate,
                fees = s.fees.toDoubleOrNull() ?: 0.0,
                stopLoss = s.stopLoss.toDoubleOrNull(),
                takeProfit = s.takeProfit.toDoubleOrNull(),
                tags = s.tags.split(",").map { it.trim() }.filter { it.isNotBlank() },
                notes = s.notes
            )
            if (s.id == 0L) repository.saveTrade(trade)
            else repository.updateTrade(trade)
            _addEditState.value = _addEditState.value.copy(isSaving = false, savedSuccessfully = true)
        }
    }

    fun deleteTrade(id: Long) {
        viewModelScope.launch { repository.deleteTrade(id) }
    }

    fun clearError() { _addEditState.value = _addEditState.value.copy(error = null) }
}
