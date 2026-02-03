package com.receiptreader.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.receiptreader.data.local.CategorySpending
import com.receiptreader.presentation.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Premium Dashboard Screen - Apple-inspired minimalist design
 */
@Composable
fun DashboardScreen(
    totalSpending: Double,
    monthlySpending: Double,
    receiptCount: Int,
    categorySpending: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
    ) {
        // Header
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { -it / 2 }
        ) {
            Header()
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Main spending card
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(delayMillis = 100)) + 
                   slideInVertically(animationSpec = tween(delayMillis = 100)) { it / 2 }
        ) {
            MainSpendingCard(totalSpending = totalSpending)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick stats row
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(delayMillis = 200)) +
                   slideInVertically(animationSpec = tween(delayMillis = 200)) { it / 2 }
        ) {
            QuickStatsRow(
                monthlySpending = monthlySpending,
                receiptCount = receiptCount
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Spending by category
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(delayMillis = 300)) +
                   slideInVertically(animationSpec = tween(delayMillis = 300)) { it / 2 }
        ) {
            SpendingByCategory(categorySpending = categorySpending)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Privacy badge
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(delayMillis = 400)) +
                   slideInVertically(animationSpec = tween(delayMillis = 400)) { it / 2 }
        ) {
            PrivacyBadge()
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "ReceiptVault",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Your spending at a glance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Profile/Settings placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MainSpendingCard(totalSpending: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 20.dp,
                spotColor = Color(0xFF007AFF).copy(alpha = 0.25f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF007AFF),
                            Color(0xFF5856D6)
                        )
                    )
                )
                .padding(28.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Total Spending",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Animated counter
                AnimatedSpendingAmount(amount = totalSpending)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Trend indicator (placeholder)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = Color(0xFF4CD964),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "On track",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedSpendingAmount(amount: Double) {
    var displayedAmount by remember { mutableStateOf(0.0) }
    
    LaunchedEffect(amount) {
        val steps = 30
        val increment = amount / steps
        for (i in 1..steps) {
            delay(20)
            displayedAmount = increment * i
        }
        displayedAmount = amount
    }
    
    Text(
        text = String.format("%.2f MAD", displayedAmount),
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        letterSpacing = (-1).sp
    )
}

@Composable
private fun QuickStatsRow(
    monthlySpending: Double,
    receiptCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            icon = Icons.Outlined.CalendarMonth,
            iconColor = Color(0xFF5856D6),
            label = "This Month",
            value = String.format("%.0f MAD", monthlySpending),
            modifier = Modifier.weight(1f)
        )
        
        QuickStatCard(
            icon = Icons.Outlined.Receipt,
            iconColor = Color(0xFF34C759),
            label = "Receipts",
            value = receiptCount.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickStatCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        iconColor.copy(alpha = 0.12f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SpendingByCategory(categorySpending: List<CategorySpending>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "By Category",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            TextButton(onClick = { }) {
                Text(
                    text = "See All",
                    color = Color(0xFF007AFF)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (categorySpending.isEmpty()) {
            // Empty state
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No spending data yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Scan your first receipt to see insights",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categorySpending) { spending ->
                    CategoryCard(
                        category = spending.category,
                        amount = spending.total,
                        color = getCategoryColor(spending.category)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: String,
    amount: Double,
    color: Color
) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Category dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = String.format("%.0f MAD", amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PrivacyBadge() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF34C759).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF34C759).copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = Color(0xFF34C759),
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "100% Private",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF248A3D)
                )
                Text(
                    text = "Your data never leaves this device",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF34C759)
                )
            }
        }
    }
}
