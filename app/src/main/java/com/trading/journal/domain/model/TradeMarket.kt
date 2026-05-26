package com.trading.journal.domain.model

enum class TradeMarket(val label: String, val icon: String) {
    STOCKS("Actions", "📈"),
    FOREX("Forex", "💱"),
    CRYPTO("Crypto", "₿"),
    FUTURES("Futures", "📊"),
    OPTIONS("Options", "🔀")
}
