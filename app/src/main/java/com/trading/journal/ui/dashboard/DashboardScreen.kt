package com.trading.journal.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trading.journal.ui.components.EquityChart
import com.trading.journal.ui.components.StatCard
import com.trading.journal.ui.components.TradeCard
import com.trading.journal.ui.theme.*
import kotlin.math.absoluteValue

@Composable
fun DashboardScreen(
    onTradeClick: (Long) -> Unit,
    onAddTrade: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item { Header(state) }
            item { EquitySection(state) }
            item { StatsGrid(state) }
            if (state.recentTrades.isNotEmpty()) {
                item { RecentHeader() }
                items(state.recentTrades, key = { it.id }) { trade ->
                    TradeCard(
                        trade = trade,
                        onClick = { onTradeClick(trade.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            } else {
                item { EmptyState(onAddTrade) }
            }
        }
    }
}

@Composable
private fun Header(state: DashboardUiState) {
    val stats = state.statistics
    val pnlColor = if (stats.totalPnl >= 0) ProfitGreen else LossRed

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Tableau de bord",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${if (stats.totalPnl >= 0) "+" else ""}${"%.2f".format(stats.totalPnl)} €",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = pnlColor
        )
        Text(
            text = "${stats.totalTrades} trades · ${stats.openTrades} ouverts",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
        )
    }
}

@Composable
private fun EquitySection(state: DashboardUiState) {
    val curve = state.statistics.equityCurve
    if (curve.size < 2) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Courbe d'équité",
            style = MaterialTheme.typography.labelLarge,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        EquityChart(
            data = curve,
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground, RoundedCornerShape(16.dp))
                .padding(12.dp)
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StatsGrid(state: DashboardUiState) {
    val stats = state.statistics
    val winRateColor = when {
        stats.winRate >= 0.6f -> ProfitGreen
        stats.winRate >= 0.4f -> Color(0xFFFFC107)
        else -> LossRed
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Statistiques",
            style = MaterialTheme.typography.labelLarge,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                label = "Win Rate",
                value = "${"%.0f".format(stats.winRate * 100)}%",
                icon = Icons.Default.ShowChart,
                valueColor = winRateColor,
                subtitle = "${stats.wins}W / ${stats.losses}L",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Profit Factor",
                value = "${"%.2f".format(stats.profitFactor)}",
                icon = Icons.Default.Balance,
                valueColor = if (stats.profitFactor >= 1.0) ProfitGreen else LossRed,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                label = "Meilleur trade",
                value = "+${"%.2f".format(stats.bestTrade)}€",
                icon = Icons.Default.EmojiEvents,
                valueColor = ProfitGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Drawdown max",
                value = "-${"%.2f".format(stats.maxDrawdown.absoluteValue)}€",
                icon = Icons.Default.TrendingDown,
                valueColor = LossRed,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun RecentHeader() {
    Text(
        "Trades récents",
        style = MaterialTheme.typography.labelLarge,
        color = OnSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
    )
}

@Composable
private fun EmptyState(onAddTrade: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.BarChart,
            contentDescription = null,
            tint = OnSurfaceVariant,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Aucun trade enregistré", color = OnSurface, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Commencez à journaliser vos trades\npour voir vos statistiques",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onAddTrade,
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Background)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Ajouter mon premier trade")
        }
    }
}
