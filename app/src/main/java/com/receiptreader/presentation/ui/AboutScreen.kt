package com.receiptreader.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * About Screen - App information, settings, and privacy details
 */
@Composable
fun AboutScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(50)
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
            AppHeader()
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Settings section
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(delayMillis = 100)) + slideInVertically(tween(delayMillis = 100)) { it / 2 }
        ) {
            SettingsSection(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Privacy section
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(delayMillis = 200)) + slideInVertically(tween(delayMillis = 200)) { it / 2 }
        ) {
            PrivacySection()
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Features section
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(delayMillis = 300)) + slideInVertically(tween(delayMillis = 300)) { it / 2 }
        ) {
            FeaturesSection()
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Legal section
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(delayMillis = 400)) + slideInVertically(tween(delayMillis = 400)) { it / 2 }
        ) {
            LegalSection()
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun AppHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF007AFF),
                            Color(0xFF5856D6)
                        )
                    ),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Receipt,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "ReceiptVault",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Privacy badge
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    Color(0xFF34C759).copy(alpha = 0.12f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF34C759)
            )
            Text(
                text = "Privacy First",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF34C759)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    SectionCard(title = "Appearance") {
        SettingsRow(
            icon = Icons.Outlined.DarkMode,
            iconColor = Color(0xFF5856D6),
            title = "Dark Mode",
            subtitle = if (isDarkMode) "On" else "Off",
            trailing = {
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onToggleDarkMode,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF007AFF),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        )
    }
}

@Composable
private fun PrivacySection() {
    SectionCard(title = "Privacy & Security") {
        PrivacyFeature(
            icon = Icons.Outlined.WifiOff,
            iconColor = Color(0xFF34C759),
            title = "No Internet Access",
            description = "App has no network permission. Your data stays on your device."
        )
        
        Divider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        PrivacyFeature(
            icon = Icons.Outlined.Smartphone,
            iconColor = Color(0xFF007AFF),
            title = "On-Device Processing",
            description = "All OCR and data processing happens locally using ML Kit."
        )
        
        Divider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        PrivacyFeature(
            icon = Icons.Outlined.Storage,
            iconColor = Color(0xFF5856D6),
            title = "Local Storage Only",
            description = "Receipts stored in encrypted app-private database."
        )
    }
}

@Composable
private fun FeaturesSection() {
    SectionCard(title = "Features") {
        FeatureRow(
            icon = Icons.Outlined.CameraAlt,
            title = "Smart Scanning",
            description = "AI-powered receipt recognition"
        )
        
        FeatureRow(
            icon = Icons.Outlined.Category,
            title = "Auto Categorization",
            description = "Automatic spending categories"
        )
        
        FeatureRow(
            icon = Icons.Outlined.BarChart,
            title = "Spending Insights",
            description = "Visual spending analytics"
        )
    }
}

@Composable
private fun LegalSection() {
    SectionCard(title = "Legal Compliance") {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ComplianceBadge(
                title = "GDPR Compliant",
                description = "Meets EU data protection requirements"
            )
            
            ComplianceBadge(
                title = "Moroccan Law 09-08",
                description = "Personal data protection compliance"
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "You have full control over your data. View, export, or delete everything at any time.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        trailing()
    }
}

@Composable
private fun PrivacyFeature(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconColor.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF007AFF),
            modifier = Modifier.size(24.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ComplianceBadge(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Verified,
            contentDescription = null,
            tint = Color(0xFF34C759),
            modifier = Modifier.size(20.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
