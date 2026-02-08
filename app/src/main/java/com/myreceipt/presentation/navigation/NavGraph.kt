package com.myreceipt.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myreceipt.presentation.ui.*
import com.myreceipt.presentation.ui.components.*
import com.myreceipt.presentation.viewmodel.ReceiptViewModel

/** Navigation destinations */
sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard :
        Screen(
            route = "dashboard",
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        )

    data object Scan :
        Screen(
            route = "scan",
            title = "Scan",
            selectedIcon = Icons.Filled.CameraAlt,
            unselectedIcon = Icons.Outlined.CameraAlt
        )

    data object Receipts :
        Screen(
            route = "receipts",
            title = "Receipts",
            selectedIcon = Icons.Filled.Receipt,
            unselectedIcon = Icons.Outlined.Receipt
        )

    data object About :
        Screen(
            route = "about",
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
}

private val screens = listOf(Screen.Dashboard, Screen.Scan, Screen.Receipts, Screen.About)

/** Main navigation for MyReceipt */
@Composable
fun MyReceiptNavGraph(viewModel: ReceiptViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Collect states
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val receipts by viewModel.receipts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val totalSpending by viewModel.totalSpending.collectAsStateWithLifecycle()
    val monthlySpending by viewModel.monthlySpending.collectAsStateWithLifecycle()
    val receiptCount by viewModel.receiptCount.collectAsStateWithLifecycle()
    val categorySpending by viewModel.categorySpending.collectAsStateWithLifecycle()
    val pendingScanResult by viewModel.pendingScanResult.collectAsStateWithLifecycle()
    val selectedReceipt by viewModel.selectedReceipt.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            PremiumBottomNavBar(
                screens = screens,
                currentDestination = currentDestination,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(
                            navController.graph.findStartDestination()
                                .id
                        ) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                enterTransition = { fadeIn(animationSpec = tween(200)) },
                exitTransition = { fadeOut(animationSpec = tween(200)) }
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        totalSpending = totalSpending,
                        monthlySpending = monthlySpending,
                        receiptCount = receiptCount,
                        categorySpending = categorySpending
                    )
                }

                composable(Screen.Scan.route) {
                    ScanScreen(
                        onReceiptCaptured = {
                                storeName,
                                date,
                                amount,
                                rawText ->
                            viewModel.setPendingScanResult(
                                storeName,
                                date,
                                amount,
                                rawText
                            )
                        }
                    )
                }

                composable(Screen.Receipts.route) {
                    ReceiptsScreen(
                        receipts = receipts,
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::setSearchQuery,
                        onReceiptClick = viewModel::setSelectedReceipt,
                        onDeleteReceipt = viewModel::deleteReceipt
                    )
                }

                composable(Screen.About.route) {
                    AboutScreen(
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = viewModel::setDarkMode
                    )
                }
            }

            // Receipt Preview Dialog (after scan)
            pendingScanResult?.let { result ->
                ReceiptPreviewDialog(
                    storeName = result.storeName,
                    date = result.date,
                    amount = result.amount,
                    rawText = result.rawText,
                    onSave = { storeName, date, amount, category ->
                        viewModel.saveReceipt(
                            storeName = storeName,
                            date = date,
                            amount = amount,
                            category = category,
                            rawText = result.rawText
                        )
                    },
                    onDismiss = viewModel::clearPendingScanResult
                )
            }

            // Receipt Detail Dialog (from history)
            selectedReceipt?.let { receipt ->
                ReceiptDetailDialog(
                    storeName = receipt.storeName,
                    date = receipt.date,
                    amount = receipt.totalAmount,
                    currency = receipt.currency,
                    category = receipt.category,
                    rawText = receipt.rawText,
                    timestamp = receipt.timestamp,
                    onEdit = { /* Future */},
                    onDelete = { viewModel.deleteReceipt(receipt) },
                    onDismiss = { viewModel.setSelectedReceipt(null) }
                )
            }
        }
    }
}

/** bottom navigation bar */
@Composable
private fun PremiumBottomNavBar(
    screens: List<Screen>,
    currentDestination: androidx.navigation.NavDestination?,
    onNavigate: (Screen) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                val isSelected =
                    currentDestination?.hierarchy?.any {
                        it.route == screen.route
                    } == true

                NavBarItem(
                    screen = screen,
                    isSelected = isSelected,
                    onClick = { onNavigate(screen) }
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(screen: Screen, isSelected: Boolean, onClick: () -> Unit) {
    val animatedWeight by
    animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "weight"
    )

    IconButton(onClick = onClick, modifier = Modifier.size(69.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier =
                Modifier.size(if (isSelected) 44.dp else 30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected)
                            Color(0xFF007AFF)
                                .copy(alpha = 0.12f)
                        else Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector =
                    if (isSelected) screen.selectedIcon
                    else screen.unselectedIcon,
                    contentDescription = screen.title,
                    tint =
                    if (isSelected) Color(0xFF007AFF)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = screen.title,
                style = MaterialTheme.typography.labelSmall,
                color =
                if (isSelected) Color(0xFF007AFF)
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
