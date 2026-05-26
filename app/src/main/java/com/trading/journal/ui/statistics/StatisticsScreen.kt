package com.trading.journal.ui.statistics

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trading.journal.domain.model.TradeStatistics
import com.trading.journal.ui.components.EquityChart
import com.trading.journal.ui.components.PnlBarChart
import com.trading.journal.ui.theme.*
import com.trading.journal.util.ChartBitmapRenderer
import com.trading.journal.util.GalleryExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Permission de stockage refusée")
            }
        }
        // User can tap the button again once permission is granted
    }

    fun saveChart(stats: TradeStatistics) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        }
        coroutineScope.launch {
            val displayMetrics = context.resources.displayMetrics
            val widthPx = displayMetrics.widthPixels
            val bitmap = withContext(Dispatchers.Default) {
                ChartBitmapRenderer.renderEquityChart(
                    equityCurve = stats.equityCurve,
                    totalPnl = stats.totalPnl,
                    maxDrawdown = stats.maxDrawdown,
                    closedTrades = stats.closedTrades,
                    widthPx = widthPx
                )
            }
            val result = GalleryExporter.saveBitmapToGallery(context, bitmap)
            snackbarHostState.showSnackbar(
                if (result.isSuccess) "Graphique enregistré dans Galerie › TradingJournal"
                else "Erreur : ${result.exceptionOrNull()?.message}"
            )
        }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        val stats = state.statistics
        if (stats.closedTrades == 0) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.BarChart, null, tint = OnSurfaceVariant, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Aucun trade clôturé", color = OnSurface, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Les statistiques apparaîtront après\nla clôture de votre premier trade",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Statistiques",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            item { EquitySection(stats, onSave = { saveChart(stats) }) }
            item { PerformanceSection(stats) }
            item { WinRateSection(stats) }
            item { SymbolsSection(stats) }
        }
    }
}

@Composable
private fun EquitySection(stats: TradeStatistics, onSave: () -> Unit) {
    StatCard(
        title = "Courbe d'équité",
        action = {
            IconButton(onClick = onSave) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Enregistrer le graphique",
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    ) {
        Spacer(Modifier.height(4.dp))
        EquityChart(
            data = stats.equityCurve,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MiniStat("P&L Total", "${if (stats.totalPnl >= 0) "+" else ""}${"%.2f".format(stats.totalPnl)}€",
                if (stats.totalPnl >= 0) ProfitGreen else LossRed)
            MiniStat("Drawdown max", "-${"%.2f".format(stats.maxDrawdown)}€", LossRed)
            MiniStat("Trades clôt.", "${stats.closedTrades}", OnBackground)
        }
    }
}

@Composable
private fun PerformanceSection(stats: TradeStatistics) {
    StatCard("Performance") {
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PerformanceItem(
                label = "Meilleur trade",
                value = "+${"%.2f".format(stats.bestTrade)}€",
                color = ProfitGreen,
                modifier = Modifier.weight(1f)
            )
            PerformanceItem(
                label = "Pire trade",
                value = "${"%.2f".format(stats.worstTrade)}€",
                color = LossRed,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PerformanceItem(
                label = "Gain moyen",
                value = "+${"%.2f".format(stats.avgWin)}€",
                color = ProfitGreen,
                modifier = Modifier.weight(1f)
            )
            PerformanceItem(
                label = "Perte moyenne",
                value = "${"%.2f".format(stats.avgLoss)}€",
                color = LossRed,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PerformanceItem(
                label = "Profit Factor",
                value = "${"%.2f".format(stats.profitFactor)}",
                color = if (stats.profitFactor >= 1) ProfitGreen else LossRed,
                modifier = Modifier.weight(1f)
            )
            PerformanceItem(
                label = "P&L moyen",
                value = "${if (stats.avgPnl >= 0) "+" else ""}${"%.2f".format(stats.avgPnl)}€",
                color = if (stats.avgPnl >= 0) ProfitGreen else LossRed,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WinRateSection(stats: TradeStatistics) {
    StatCard("Win Rate") {
        Spacer(Modifier.height(12.dp))
        // Win rate bar
        val winRate = stats.winRate
        val winColor = when {
            winRate >= 0.6f -> ProfitGreen
            winRate >= 0.4f -> Color(0xFFFFC107)
            else -> LossRed
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${stats.wins}W",
                fontWeight = FontWeight.Bold,
                color = ProfitGreen,
                fontSize = 18.sp
            )
            Text(
                "${"%.1f".format(winRate * 100)}%",
                fontWeight = FontWeight.ExtraBold,
                color = winColor,
                fontSize = 28.sp
            )
            Text(
                "${stats.losses}L",
                fontWeight = FontWeight.Bold,
                color = LossRed,
                fontSize = 18.sp
            )
        }
        Spacer(Modifier.height(10.dp))
        // Segmented bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            if (stats.closedTrades > 0) {
                Box(
                    modifier = Modifier
                        .weight(winRate.coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(ProfitGreen)
                )
                Box(
                    modifier = Modifier
                        .weight((1f - winRate).coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(LossRed)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Victoires", style = MaterialTheme.typography.bodySmall, color = ProfitGreen)
            Text("Défaites", style = MaterialTheme.typography.bodySmall, color = LossRed)
        }
    }
}

@Composable
private fun SymbolsSection(stats: TradeStatistics) {
    if (stats.pnlBySymbol.isEmpty()) return
    StatCard("P&L par symbole") {
        Spacer(Modifier.height(8.dp))
        PnlBarChart(
            data = stats.pnlBySymbol,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    action: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = OnBackground, fontWeight = FontWeight.SemiBold)
            action?.invoke()
        }
        content()
    }
}

@Composable
private fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 15.sp)
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
    }
}

@Composable
private fun PerformanceItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceVariant, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
    }
}
