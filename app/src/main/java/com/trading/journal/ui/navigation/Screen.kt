package com.trading.journal.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object TradeList : Screen("trades")
    object Statistics : Screen("statistics")
    object AddTrade : Screen("trades/add")
    object EditTrade : Screen("trades/edit/{tradeId}") {
        fun createRoute(tradeId: Long) = "trades/edit/$tradeId"
    }
    object TradeDetail : Screen("trades/detail/{tradeId}") {
        fun createRoute(tradeId: Long) = "trades/detail/$tradeId"
    }
}
