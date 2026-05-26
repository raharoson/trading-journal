package com.trading.journal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trading.journal.domain.model.Trade
import com.trading.journal.domain.model.TradeDirection
import com.trading.journal.ui.theme.*
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

@Composable
fun TradeCard(
    trade: Trade,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pnl = trade.pnl
    val isProfit = pnl != null && pnl > 0
    val pnlColor = when {
        pnl == null -> OnSurfaceVariant
        isProfit -> ProfitGreen
        else -> LossRed
    }
    val directionColor = if (trade.direction == TradeDirection.LONG) ProfitGreen else LossRed
    val borderColor = when {
        trade.isOpen -> Primary.copy(alpha = 0.3f)
        isProfit -> ProfitGreen.copy(alpha = 0.2f)
        else -> LossRed.copy(alpha = 0.2f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Direction indicator bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(44.dp)
                .background(directionColor, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = trade.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Spacer(Modifier.width(8.dp))
                DirectionChip(trade.direction)
                if (trade.isOpen) {
                    Spacer(Modifier.width(6.dp))
                    OpenBadge()
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trade.market.icon + " " + trade.market.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
                Text("·", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = trade.entryDate.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }

        // P&L column
        Column(horizontalAlignment = Alignment.End) {
            if (pnl != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isProfit) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = pnlColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = formatPnl(pnl),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = pnlColor
                    )
                }
                trade.pnlPercent?.let { pct ->
                    Text(
                        text = "${if (pct >= 0) "+" else ""}${"%.2f".format(pct)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = pnlColor.copy(alpha = 0.8f)
                    )
                }
            } else {
                Text(
                    text = "Ouvert",
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary
                )
                Text(
                    text = "@ ${"%.4f".format(trade.entryPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DirectionChip(direction: TradeDirection) {
    val bg = if (direction == TradeDirection.LONG)
        ProfitGreenDim else LossRedDim
    val fg = if (direction == TradeDirection.LONG) ProfitGreen else LossRed
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = direction.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun OpenBadge() {
    Box(
        modifier = Modifier
            .background(PrimaryContainer, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "OUVERT",
            style = MaterialTheme.typography.labelSmall,
            color = Primary,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatPnl(pnl: Double): String {
    val prefix = if (pnl >= 0) "+" else ""
    return "$prefix${"%.2f".format(pnl)} €"
}
