# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (ProGuard enabled)
./gradlew test                   # Unit tests
./gradlew connectedAndroidTest   # Instrumented tests (device/emulator required)
./gradlew lint                   # Lint checks
./gradlew clean                  # Clean build artifacts
```

Single test class:
```bash
./gradlew test --tests "com.trading.journal.TradeStatisticsTest"
```

- minSdk 26, targetSdk/compileSdk 34, Java 1.8, Kotlin 1.9.24
- KSP (not KAPT) for Room and Hilt annotation processing

## Architecture

Clean Architecture with 3 layers — dependency rule enforced (domain has zero Android imports):

```
domain/model/       ← pure Kotlin, no Android deps
data/               ← Room entities + DAO + repository impl
ui/                 ← Compose screens + ViewModels
di/                 ← Hilt modules (wiring only)
```

**Data flow:**
```
Room DAO (Flow<List<TradeEntity>>)
  → TradeRepositoryImpl.map { it.toDomain() }
  → ViewModel.stateIn(viewModelScope, WhileSubscribed(5_000))
  → Composable.collectAsStateWithLifecycle()
```

**Hilt wiring:** `DatabaseModule` provides `AppDatabase` and `TradeDao`, and binds `TradeRepositoryImpl → TradeRepository`. ViewModels are `@HiltViewModel` injected via `hiltViewModel()`.

## Key Domain Logic

**`Trade` (domain/model/Trade.kt):** computed properties on the data class:
- `pnl` — direction-aware: `(exit - entry) * qty - fees` for LONG, reversed for SHORT
- `rMultiple` — `pnl / (entry - stopLoss) * qty`
- `isOpen` — `exitPrice == null`

**`TradeStatistics` (domain/model/TradeStatistics.kt):** built from a list of closed trades — calculates win rate, profit factor, max drawdown (peak-to-trough), equity curve (cumulative P&L list), and P&L grouped by symbol.

## Database

Single Room entity `TradeEntity` ↔ domain `Trade` via `toDomain()` / `fromDomain()`. Dates stored as epoch milliseconds (Long). Tags stored as CSV string, split into `List<String>` on read.

DAO queries return `Flow<List<TradeEntity>>` for reactive updates, except `getTradeById` which is `suspend`.

## Navigation

Three bottom-nav tabs: **Dashboard → TradeList → Statistics**. Three full-screen routes: **AddTrade**, **EditTrade/{tradeId}**, **TradeDetail/{tradeId}**. All routes defined in `Screen.kt` sealed class; wired in `NavGraph.kt`.

## UI Conventions

- All screens follow `Screen(viewModel: XViewModel = hiltViewModel())` signature
- UiState is a data class held in `StateFlow`, mapped from repository `Flow` via `.stateIn()`
- `TradeViewModel` manages two separate state flows: `listUiState` (filters/sort/search) and `addEditState` (form)
- Custom Canvas charts in `EquityChart.kt` (Bezier curve with gradient) and `PnlBarChart`
- Dark terminal theme: background `#0A0E17`, primary cyan `#4FC3F7`, profit `#00E676`, loss `#FF5252`

## Versions (libs.versions.toml)

| Library | Version |
|---------|---------|
| Compose BOM | 2024.06.00 |
| Room | 2.6.1 |
| Hilt | 2.51.1 |
| Navigation Compose | 2.7.7 |
| Lifecycle | 2.8.3 |
