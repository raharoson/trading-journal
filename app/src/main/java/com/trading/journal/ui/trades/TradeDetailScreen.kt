package com.trading.journal.ui.trades

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trading.journal.domain.model.Trade
import com.trading.journal.domain.model.TradeDirection
import com.trading.journal.ui.theme.*
import java.time.format.DateTimeFormatter

private val dtFmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeDetailScreen(
    tradeId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: TradeViewModel = hiltViewModel()
) {
    var trade by remember { mutableStateOf<Trade?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val listState by viewModel.listUiState.collectAsStateWithLifecycle()
    LaunchedEffect(listState.trades, tradeId) {
        trade = listState.trades.find { it.id == tradeId }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = SurfaceVariant,
            title = { Text("Supprimer le trade", color = OnBackground) },
            text = { Text("Cette action est irréversible.", color = OnSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTrade(tradeId)
                    showDeleteDialog = false
                    onBack()
                }) { Text("Supprimer", color = LossRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler", color = OnSurfaceVariant)
                }
            }
        )
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        trade?.symbol ?: "Détail",
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = OnBackground)
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(tradeId) }) {
                        Icon(Icons.Default.Edit, null, tint = Primary)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, null, tint = LossRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        val t = trade
        if (t == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // P&L Hero card
            PnlHeroCard(t)

            // Trade info
            DetailSection("Informations") {
                DetailRow(Icons.Default.Label, "Symbole", t.symbol)
                DetailRow(
                    Icons.Default.SwapVert, "Direction",
                    t.direction.label,
                    valueColor = if (t.direction == TradeDirection.LONG) ProfitGreen else LossRed
                )
                DetailRow(Icons.Default.Category, "Marché", "${t.market.icon} ${t.market.label}")
                DetailRow(Icons.Default.Numbers, "Quantité", t.quantity.toString())
                DetailRow(Icons.Default.AccountBalanceWallet, "Valeur notionnelle",
                    "${"%.2f".format(t.notionalValue)} €")
            }

            // Prices
            DetailSection("Prix") {
                DetailRow(Icons.Default.Login, "Prix entrée", "${"%.5f".format(t.entryPrice)}")
                DetailRow(Icons.Default.Logout, "Prix sortie",
                    t.exitPrice?.let { "${"%.5f".format(it)}" } ?: "Ouvert")
                if (t.stopLoss != null)
                    DetailRow(Icons.Default.SecurityUpdateWarning, "Stop Loss",
                        "${"%.5f".format(t.stopLoss)}", valueColor = LossRed)
                if (t.takeProfit != null)
                    DetailRow(Icons.Default.Flag, "Take Profit",
                        "${"%.5f".format(t.takeProfit)}", valueColor = ProfitGreen)
                if (t.fees > 0)
                    DetailRow(Icons.Default.Receipt, "Frais", "${"%.2f".format(t.fees)} €")
            }

            // Dates
            DetailSection("Dates") {
                DetailRow(Icons.Default.Login, "Entrée", t.entryDate.format(dtFmt))
                if (t.exitDate != null)
                    DetailRow(Icons.Default.Logout, "Sortie", t.exitDate.format(dtFmt))
            }

            // R-Multiple
            if (t.rMultiple != null) {
                DetailSection("Risque") {
                    DetailRow(
                        Icons.Default.Balance, "R-Multiple",
                        "${"%.2f".format(t.rMultiple)}R",
                        valueColor = if ((t.rMultiple) >= 0) ProfitGreen else LossRed
                    )
                }
            }

            // Tags
            if (t.tags.isNotEmpty()) {
                DetailSection("Tags") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        t.tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .background(PrimaryContainer, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(tag, style = MaterialTheme.typography.labelMedium, color = Primary)
                            }
                        }
                    }
                }
            }

            // Notes
            if (t.notes.isNotBlank()) {
                DetailSection("Notes") {
                    Text(t.notes, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PnlHeroCard(trade: Trade) {
    val pnl = trade.pnl
    val isProfit = pnl != null && pnl > 0
    val bg = when {
        pnl == null -> CardBackground
        isProfit -> ProfitGreenDim
        else -> LossRedDim
    }
    val fg = when {
        pnl == null -> Primary
        isProfit -> ProfitGreen
        else -> LossRed
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(16.dp))
            .border(1.dp, fg.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "P&L réalisé",
            style = MaterialTheme.typography.bodySmall,
            color = fg.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (pnl != null) "${if (pnl >= 0) "+" else ""}${"%.2f".format(pnl)} €"
            else "Trade ouvert",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = fg
        )
        if (trade.pnlPercent != null) {
            Text(
                text = "${if (trade.pnlPercent!! >= 0) "+" else ""}${"%.2f".format(trade.pnlPercent)}%",
                style = MaterialTheme.typography.bodyLarge,
                color = fg.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = OnBackground
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = valueColor)
    }
}
