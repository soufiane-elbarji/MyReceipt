package com.receiptreader.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.receiptreader.data.local.CategorySpending
import com.receiptreader.presentation.ui.theme.getCategoryColor

/**
 * Animated donut chart for spending by category.
 * Premium visualization with smooth animations.
 */
@Composable
fun DonutChart(
    data: List<CategorySpending>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 200.dp,
    strokeWidth: Dp = 32.dp
) {
    val total = data.sumOf { it.total }
    if (total <= 0 || data.isEmpty()) {
        // Empty state
        Box(
            modifier = modifier.size(chartSize),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }
    
    // Animate the chart drawing
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "chartAnimation"
    )
    
    Box(
        modifier = modifier.size(chartSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
            
            val radius = (size.minDimension - strokeWidth.toPx()) / 2
            val center = Offset(size.width / 2, size.height / 2)
            
            var startAngle = -90f
            
            data.forEach { categorySpending ->
                val sweepAngle = ((categorySpending.total / total) * 360f * animationProgress).toFloat()
                val color = getCategoryColor(categorySpending.category)
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = stroke
                )
                
                startAngle += sweepAngle
            }
        }
    }
}

/**
 * Legend item for donut chart.
 */
@Composable
fun ChartLegend(
    data: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.total }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.forEach { categorySpending ->
                LegendItem(
                    color = getCategoryColor(categorySpending.category),
                    label = categorySpending.category,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(color = color)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Simple wrapper for horizontal arrangement
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

/**
 * Animated counter for stats display.
 */
@Composable
fun AnimatedCounter(
    targetValue: Double,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    durationMillis: Int = 1000
) {
    var animatedValue by remember { mutableDoubleStateOf(0.0) }
    
    LaunchedEffect(targetValue) {
        animate(
            initialValue = 0f,
            targetValue = targetValue.toFloat(),
            animationSpec = tween(durationMillis, easing = EaseOutCubic)
        ) { value, _ ->
            animatedValue = value.toDouble()
        }
    }
    
    Text(
        text = "$prefix${String.format("%.2f", animatedValue)}$suffix",
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

/**
 * Animated integer counter.
 */
@Composable
fun AnimatedIntCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    durationMillis: Int = 800
) {
    var animatedValue by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(targetValue) {
        animate(
            initialValue = 0f,
            targetValue = targetValue.toFloat(),
            animationSpec = tween(durationMillis, easing = EaseOutCubic)
        ) { value, _ ->
            animatedValue = value.toInt()
        }
    }
    
    Text(
        text = animatedValue.toString(),
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

private val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
