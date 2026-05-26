package com.trading.journal.ui.trades

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trading.journal.ui.components.TradeCard
import com.trading.journal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeListScreen(
    onTradeClick: (Long) -> Unit,
    onAddTrade: () -> Unit,
    viewModel: TradeViewModel = hiltViewModel()
) {
    val state by viewModel.listUiState.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            Column(
                modifier = Modifier
                    .background(Background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Trades",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Row {
                        IconButton(onClick = { showSearch = !showSearch }) {
                            Icon(Icons.Default.Search, contentDescription = "Rechercher", tint = OnSurfaceVariant)
                        }
                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(Icons.Default.Sort, contentDescription = "Trier", tint = OnSurfaceVariant)
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false },
                                modifier = Modifier.background(SurfaceVariant)
                            ) {
                                SortOption.entries.forEach { opt ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                sortLabel(opt),
                                                color = if (state.sort == opt) Primary else OnSurface
                                            )
                                        },
                                        onClick = {
                                            viewModel.setSort(opt)
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = showSearch) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        placeholder = { Text("Symbole, tag…", color = OnSurfaceVariant) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = OnSurfaceVariant) },
                        trailingIcon = {
                            if (state.searchQuery.isNotBlank())
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, null, tint = OnSurfaceVariant)
                                }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = OnBackground,
                            unfocusedTextColor = OnBackground,
                            cursorColor = Primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(8.dp))
                FilterChips(
                    selected = state.filter,
                    onSelect = viewModel::setFilter
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTrade,
                containerColor = Primary,
                contentColor = Background,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter trade")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        if (state.trades.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Inbox,
                        null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Aucun trade trouvé", color = OnSurfaceVariant)
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = 8.dp, bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "${state.trades.size} trade${if (state.trades.size > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            items(state.trades, key = { it.id }) { trade ->
                TradeCard(trade = trade, onClick = { onTradeClick(trade.id) })
            }
        }
    }
}

@Composable
private fun FilterChips(
    selected: TradeFilter,
    onSelect: (TradeFilter) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(TradeFilter.entries.toList()) { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelect(filter) },
                label = { Text(filterLabel(filter)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryContainer,
                    selectedLabelColor = Primary,
                    containerColor = SurfaceVariant,
                    labelColor = OnSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = filter == selected,
                    selectedBorderColor = Primary.copy(alpha = 0.5f),
                    borderColor = CardBorder
                )
            )
        }
    }
}

private fun filterLabel(f: TradeFilter) = when (f) {
    TradeFilter.ALL -> "Tous"
    TradeFilter.OPEN -> "Ouverts"
    TradeFilter.CLOSED -> "Fermés"
    TradeFilter.LONG -> "Long"
    TradeFilter.SHORT -> "Short"
}

private fun sortLabel(s: SortOption) = when (s) {
    SortOption.DATE_DESC -> "Date (récent)"
    SortOption.DATE_ASC -> "Date (ancien)"
    SortOption.PNL_DESC -> "P&L (meilleur)"
    SortOption.PNL_ASC -> "P&L (pire)"
    SortOption.SYMBOL -> "Symbole A-Z"
}
