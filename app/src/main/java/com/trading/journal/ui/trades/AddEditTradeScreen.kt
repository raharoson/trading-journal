package com.trading.journal.ui.trades

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trading.journal.domain.model.TradeDirection
import com.trading.journal.domain.model.TradeMarket
import com.trading.journal.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTradeScreen(
    tradeId: Long?,
    onBack: () -> Unit,
    viewModel: TradeViewModel = hiltViewModel()
) {
    val state by viewModel.addEditState.collectAsStateWithLifecycle()
    val isEdit = tradeId != null && tradeId != 0L

    LaunchedEffect(tradeId) {
        if (isEdit) viewModel.loadTradeForEdit(tradeId!!)
        else viewModel.resetAddEditState()
    }

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) onBack()
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEdit) "Modifier le trade" else "Nouveau trade",
                        fontWeight = FontWeight.SemiBold,
                        color = OnBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = OnBackground)
                    }
                },
                actions = {
                    TextButton(
                        onClick = viewModel::saveTrade,
                        enabled = !state.isSaving
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Enregistrer", color = Primary, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = LossRedDim),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = LossRed, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(state.error!!, color = LossRed, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = viewModel::clearError, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, null, tint = LossRed)
                        }
                    }
                }
            }

            // Symbol
            FormSection("Instrument") {
                TradeTextField(
                    value = state.symbol,
                    onValueChange = { viewModel.updateField { copy(symbol = it.uppercase()) } },
                    label = "Symbole (ex: AAPL, BTC/USDT)",
                    leadingIcon = Icons.Default.Search
                )
            }

            // Market
            FormSection("Marché") {
                MarketSelector(
                    selected = state.market,
                    onSelect = { viewModel.updateField { copy(market = it) } }
                )
            }

            // Direction
            FormSection("Direction") {
                DirectionToggle(
                    direction = state.direction,
                    onSelect = { viewModel.updateField { copy(direction = it) } }
                )
            }

            // Prices
            FormSection("Prix & Quantité") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TradeTextField(
                        value = state.entryPrice,
                        onValueChange = { viewModel.updateField { copy(entryPrice = it) } },
                        label = "Prix entrée *",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                    TradeTextField(
                        value = state.exitPrice,
                        onValueChange = { viewModel.updateField { copy(exitPrice = it) } },
                        label = "Prix sortie",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TradeTextField(
                        value = state.quantity,
                        onValueChange = { viewModel.updateField { copy(quantity = it) } },
                        label = "Quantité *",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                    TradeTextField(
                        value = state.fees,
                        onValueChange = { viewModel.updateField { copy(fees = it) } },
                        label = "Frais (€)",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Risk management
            FormSection("Gestion du risque") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TradeTextField(
                        value = state.stopLoss,
                        onValueChange = { viewModel.updateField { copy(stopLoss = it) } },
                        label = "Stop Loss",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                    TradeTextField(
                        value = state.takeProfit,
                        onValueChange = { viewModel.updateField { copy(takeProfit = it) } },
                        label = "Take Profit",
                        keyboardType = KeyboardType.Decimal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Dates
            FormSection("Dates") {
                DateTimeField(
                    label = "Date d'entrée",
                    value = state.entryDate,
                    onValueChange = { viewModel.updateField { copy(entryDate = it) } }
                )
                Spacer(Modifier.height(8.dp))
                DateTimeField(
                    label = "Date de sortie (optionnel)",
                    value = state.exitDate,
                    onValueChange = { viewModel.updateField { copy(exitDate = it) } },
                    nullable = true
                )
            }

            // Tags & Notes
            FormSection("Tags & Notes") {
                TradeTextField(
                    value = state.tags,
                    onValueChange = { viewModel.updateField { copy(tags = it) } },
                    label = "Tags (séparés par virgule)",
                    leadingIcon = Icons.Default.Tag
                )
                Spacer(Modifier.height(8.dp))
                TradeTextField(
                    value = state.notes,
                    onValueChange = { viewModel.updateField { copy(notes = it) } },
                    label = "Notes / Analyse",
                    leadingIcon = Icons.Default.Notes,
                    singleLine = false,
                    minLines = 3
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TradeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        leadingIcon = if (leadingIcon != null) {
            { Icon(leadingIcon, null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp)) }
        } else null,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = CardBorder,
            focusedLabelColor = Primary,
            unfocusedLabelColor = OnSurfaceVariant,
            focusedTextColor = OnBackground,
            unfocusedTextColor = OnBackground,
            cursorColor = Primary
        ),
        shape = RoundedCornerShape(10.dp),
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
private fun DirectionToggle(direction: TradeDirection, onSelect: (TradeDirection) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(10.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TradeDirection.entries.forEach { d ->
            val isSelected = d == direction
            val bg = when {
                isSelected && d == TradeDirection.LONG -> ProfitGreenDim
                isSelected && d == TradeDirection.SHORT -> LossRedDim
                else -> Color.Transparent
            }
            val fg = when {
                isSelected && d == TradeDirection.LONG -> ProfitGreen
                isSelected && d == TradeDirection.SHORT -> LossRed
                else -> OnSurfaceVariant
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bg)
                    .clickable { onSelect(d) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (d == TradeDirection.LONG) "▲ LONG" else "▼ SHORT",
                    color = fg,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun MarketSelector(selected: TradeMarket, onSelect: (TradeMarket) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TradeMarket.entries.forEach { market ->
            val isSelected = market == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(market) },
                label = { Text(market.icon + " " + market.label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryContainer,
                    selectedLabelColor = Primary,
                    containerColor = SurfaceVariant,
                    labelColor = OnSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    selectedBorderColor = Primary.copy(alpha = 0.5f),
                    borderColor = CardBorder
                )
            )
        }
    }
}

@Composable
private fun DateTimeField(
    label: String,
    value: LocalDateTime?,
    onValueChange: (LocalDateTime?) -> Unit,
    nullable: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }
    var inputText by remember(value) {
        mutableStateOf(value?.format(dtFormatter) ?: "")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = { text ->
                inputText = text
                runCatching { LocalDateTime.parse(text, dtFormatter) }
                    .onSuccess { onValueChange(it) }
            },
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            placeholder = { Text("jj/MM/aaaa HH:mm", color = OnSurfaceVariant) },
            leadingIcon = {
                Icon(Icons.Default.DateRange, null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
            },
            trailingIcon = if (nullable && value != null) {
                {
                    IconButton(onClick = { onValueChange(null); inputText = "" }) {
                        Icon(Icons.Default.Clear, null, tint = OnSurfaceVariant)
                    }
                }
            } else null,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = CardBorder,
                focusedLabelColor = Primary,
                unfocusedLabelColor = OnSurfaceVariant,
                focusedTextColor = OnBackground,
                unfocusedTextColor = OnBackground,
                cursorColor = Primary
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (!nullable || value == null) {
            IconButton(
                onClick = { onValueChange(LocalDateTime.now()); inputText = LocalDateTime.now().format(dtFormatter) },
                modifier = Modifier
                    .background(PrimaryContainer, RoundedCornerShape(8.dp))
                    .size(48.dp)
            ) {
                Icon(Icons.Default.AccessTime, null, tint = Primary)
            }
        }
    }
}
