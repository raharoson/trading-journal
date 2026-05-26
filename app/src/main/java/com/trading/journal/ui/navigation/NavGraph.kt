package com.trading.journal.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.trading.journal.ui.dashboard.DashboardScreen
import com.trading.journal.ui.statistics.StatisticsScreen
import com.trading.journal.ui.theme.*
import com.trading.journal.ui.trades.*

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Dashboard", Icons.Default.Dashboard),
    BottomNavItem(Screen.TradeList, "Trades", Icons.Default.FormatListBulleted),
    BottomNavItem(Screen.Statistics, "Stats", Icons.Default.BarChart)
)

@Composable
fun TradingJournalNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any {
        currentDestination?.hierarchy?.any { d -> d.route == it.screen.route } == true
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                indicatorColor = PrimaryContainer,
                                unselectedIconColor = OnSurfaceVariant,
                                unselectedTextColor = OnSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onTradeClick = { id -> navController.navigate(Screen.TradeDetail.createRoute(id)) },
                    onAddTrade = { navController.navigate(Screen.AddTrade.route) }
                )
            }

            composable(Screen.TradeList.route) {
                TradeListScreen(
                    onTradeClick = { id -> navController.navigate(Screen.TradeDetail.createRoute(id)) },
                    onAddTrade = { navController.navigate(Screen.AddTrade.route) }
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }

            composable(Screen.AddTrade.route) {
                AddEditTradeScreen(
                    tradeId = null,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditTrade.route,
                arguments = listOf(navArgument("tradeId") { type = NavType.LongType })
            ) { backStackEntry ->
                val tradeId = backStackEntry.arguments?.getLong("tradeId") ?: 0L
                AddEditTradeScreen(
                    tradeId = tradeId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.TradeDetail.route,
                arguments = listOf(navArgument("tradeId") { type = NavType.LongType })
            ) { backStackEntry ->
                val tradeId = backStackEntry.arguments?.getLong("tradeId") ?: 0L
                TradeDetailScreen(
                    tradeId = tradeId,
                    onBack = { navController.popBackStack() },
                    onEdit = { id ->
                        navController.navigate(Screen.EditTrade.createRoute(id))
                    }
                )
            }
        }
    }
}
