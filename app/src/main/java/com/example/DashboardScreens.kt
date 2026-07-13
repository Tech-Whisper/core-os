package com.example

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun QuickActionButton(
    title: String,
    subtitle: String = "",
    icon: ImageVector,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassBg)
            .border(BorderStroke(1.dp, Color.White.copy(alpha=0.1f)), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = LocalAccentColor.current,
                    strokeWidth = 2.dp
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LocalAccentColor.current.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = LocalAccentColor.current, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = LightGrey,
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

val LocalAccentColor = compositionLocalOf { NeonBlue }

@Composable
fun SystemBottomNavigation(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(86.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(CardBg)
            .drawBehind {
                drawLine(
                    color = SurfaceContainerHigh,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2f
                )
            }
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onTabSelected(Screen.HomeDashboard) }
                .testTag("tab_system"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isSelected = currentScreen == Screen.HomeDashboard
            Box(
                modifier = Modifier
                    .size(64.dp, 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) LocalAccentColor.current.copy(alpha = 0.15f) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Dashboard,
                    contentDescription = null,
                    tint = if (isSelected) LocalAccentColor.current else MutedGrey,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "SYSTEM",
                color = if (isSelected) LocalAccentColor.current else MutedGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onTabSelected(Screen.LiveMonitor) }
                .testTag("tab_metrics"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isSelected = currentScreen == Screen.LiveMonitor || currentScreen == Screen.SystemDashboard
            Box(
                modifier = Modifier
                    .size(64.dp, 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) LocalAccentColor.current.copy(alpha = 0.15f) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Timeline,
                    contentDescription = null,
                    tint = if (isSelected) LocalAccentColor.current else MutedGrey,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "METRICS",
                color = if (isSelected) LocalAccentColor.current else MutedGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onTabSelected(Screen.NotificationsPreview) }
                .testTag("tab_logs"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isSelected = currentScreen == Screen.NotificationsPreview
            Box(
                modifier = Modifier
                    .size(64.dp, 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) LocalAccentColor.current.copy(alpha = 0.15f) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                    tint = if (isSelected) LocalAccentColor.current else MutedGrey,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "LOGS",
                color = if (isSelected) LocalAccentColor.current else MutedGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onTabSelected(Screen.SystemDashboard) }
                .testTag("tab_config"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isSelected = currentScreen == Screen.SystemDashboard
            Box(
                modifier = Modifier
                    .size(64.dp, 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) LocalAccentColor.current.copy(alpha = 0.15f) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SettingsInputComponent,
                    contentDescription = null,
                    tint = if (isSelected) LocalAccentColor.current else MutedGrey,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "CONFIG",
                color = if (isSelected) LocalAccentColor.current else MutedGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MetricCircleIndicator(
    percentage: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceContainer)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val innerSize = size.minDimension - strokeWidth
            
            // Draw track
            drawCircle(
                color = SurfaceContainerHigh,
                radius = innerSize / 2f,
                style = Stroke(strokeWidth)
            )

            // Draw progress circle
            val coercedPercentage = percentage.coerceIn(0, 100)
            val sweepAngle = (coercedPercentage / 100f) * 360f
            drawArc(
                brush = Brush.linearGradient(listOf(NeonBlue, NeonBlue)),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                size = Size(innerSize, innerSize),
                style = Stroke(strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                topLeft = Offset((size.width - innerSize)/2f, (size.height - innerSize)/2f)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (percentage < 0) "..." else "$percentage%",
                color = if (percentage < 0) MutedGrey else NeonBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (percentage < 0) "WAITING" else label,
                color = MutedGrey,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MetricSmallCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Column(
        modifier = Modifier
            .background(SurfaceContainerHigh, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, color = MutedGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DashboardHeader(viewModel: MainViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back",
                tint = LocalAccentColor.current,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { viewModel.navigateBack() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "CORE_OS",
                color = LocalAccentColor.current,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        // Profile Avatar Icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(LocalAccentColor.current.copy(alpha=0.15f))
                .clickable { viewModel.navigateTo(Screen.SystemDashboard) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = LocalAccentColor.current,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HomeDashboardScreen(viewModel: MainViewModel) {
    val cpu by viewModel.cpuLoad.collectAsStateWithLifecycle()
    val ram by viewModel.ramLoad.collectAsStateWithLifecycle()
    val pwr by viewModel.pwrLoad.collectAsStateWithLifecycle()
    val procs by viewModel.processes.collectAsStateWithLifecycle()
    val rawAlerts by viewModel.alerts.collectAsStateWithLifecycle()
    val sysBattery by viewModel.sysBattery.collectAsStateWithLifecycle()
    
    var showDetails by remember { mutableStateOf(false) }
    var isOptimizing by remember { mutableStateOf(false) }
    var optimized by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val vitalityScore = 100 - ((cpu + ram) / 2).coerceIn(0, 100)
    val isOptimal = vitalityScore >= 75

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            DashboardHeader(viewModel = viewModel)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                if (sysBattery in 1..19) {
                    item { PowerSaverToggle() }
                }

                // Tiered Discovery: System Vitality Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(GlassBg)
                            .border(BorderStroke(1.dp, Color.White.copy(alpha=0.1f)), RoundedCornerShape(24.dp))
                            .clickable { showDetails = !showDetails }
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "DEVICE HEALTH SCORE",
                                color = MutedGrey,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
                                CircularProgressIndicator(
                                    progress = { vitalityScore / 100f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = if (isOptimal) LocalAccentColor.current else CyberRed,
                                    strokeWidth = 10.dp,
                                    trackColor = CoreBorder,
                                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$vitalityScore",
                                        color = Color.White,
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "/100",
                                        color = LightGrey,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = if (isOptimal) "Systems operating within normal parameters." else "Elevated resource consumption detected.",
                                color = LightGrey,
                                fontSize = 15.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (showDetails) "Hide Deep Dive" else "Deep Dive",
                                    color = LocalAccentColor.current,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (showDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = LocalAccentColor.current,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    val context = LocalContext.current
                    var coolingDown by remember { mutableStateOf(false) }
                    var batteryOptimizing by remember { mutableStateOf(false) }
                    var networkScanning by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionButton(
                            title = "Cool Down",
                            icon = Icons.Default.Delete, // Represents memory clear
                            isLoading = coolingDown,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                coolingDown = true
                                scope.launch {
                                    // Kill all background processes we can
                                    val topCpuApp = procs.maxByOrNull { it.currentCpu }
                                    if (topCpuApp != null) {
                                        viewModel.killProcess(context, topCpuApp.pid)
                                    }
                                    delay(1000)
                                    coolingDown = false
                                    android.widget.Toast.makeText(context, "System cooled down", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        QuickActionButton(
                            title = "Battery",
                            subtitle = "Optimize",
                            icon = Icons.Default.Build, // Standard icon for tools/fixing
                            isLoading = batteryOptimizing,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                batteryOptimizing = true
                                scope.launch {
                                    delay(800)
                                    batteryOptimizing = false
                                    android.widget.Toast.makeText(context, "Battery rules applied", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        QuickActionButton(
                            title = "Network",
                            subtitle = "Scan",
                            icon = Icons.Default.Search,
                            isLoading = networkScanning,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                networkScanning = true
                                scope.launch {
                                    viewModel.refreshProcesses(context)
                                    delay(1500)
                                    networkScanning = false
                                    android.widget.Toast.makeText(context, "Network dependencies scanned", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }

                item {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showDetails,
                        enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                        exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            // Hardware Deep-Dive (Radial Progress Rings)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MetricCircleIndicator(percentage = cpu, label = "SoC", modifier = Modifier.weight(1f))
                                MetricCircleIndicator(percentage = ram, label = "RAM", modifier = Modifier.weight(1f))
                                MetricCircleIndicator(percentage = pwr, label = "BAT", modifier = Modifier.weight(1f))
                            }

                            if (sysBattery >= 20 || sysBattery == 0) {
                                PowerSaverToggle()
                            }

                            // Actionable Alerts
                            val maxCpuProc = procs.maxByOrNull { if (it.currentCpu.isNaN() || it.currentCpu.isInfinite()) 0.0 else it.currentCpu }
                            val isHighCpu = cpu > 70
                            if ((!isOptimal || isHighCpu) && !optimized) {
                                val currentContext = LocalContext.current
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(CyberRed.copy(alpha=0.1f))
                                        .border(BorderStroke(1.dp, CyberRed.copy(alpha=0.3f)), RoundedCornerShape(16.dp))
                                        .padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = "Alert", tint = CyberRed)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(if (isHighCpu) "High CPU Activity" else "System Suboptimal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (isHighCpu && maxCpuProc != null) "App causing load: ${maxCpuProc.name}\nSuggest Force Stop or Clear Cache." else "Background services causing excessive wakelocks.",
                                                color = LightGrey,
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        AnimatedContent(
                                            targetState = when {
                                                optimized -> 2
                                                isOptimizing -> 1
                                                else -> 0
                                            },
                                            label = "optimize_btn",
                                            transitionSpec = {
                                                fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                                            }
                                        ) { state ->
                                            when (state) {
                                                2 -> Icon(Icons.Default.Check, contentDescription = "Optimized", tint = LocalAccentColor.current, modifier = Modifier.size(24.dp))
                                                1 -> CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CyberRed, strokeWidth = 2.dp)
                                                else -> TextButton(
                                                    onClick = {
                                                        isOptimizing = true
                                                        scope.launch {
                                                            delay(1200)
                                                            isOptimizing = false
                                                            optimized = true
                                                            if (maxCpuProc != null) {
                                                                viewModel.killProcess(currentContext, maxCpuProc.pid)
                                                            }
                                                        }
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                                    modifier = Modifier.height(32.dp).border(1.dp, CyberRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                                ) {
                                                    Text(if (isHighCpu && maxCpuProc != null) "Force Stop" else "Optimize", color = CyberRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                            // Section title HIGH ENERGY CONSUMERS
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                                    Icon(Icons.Default.Speed, "consumers", tint = LocalAccentColor.current, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "HIGH ENERGY CONSUMERS",
                                        color = MutedGrey,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                // Grouped processes (Top 3 only)
                                val sortedByCpu = procs.sortedByDescending { it.currentCpu }
                                sortedByCpu.take(3).forEach { proc ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(SurfaceContainer)
                                            .clickable { viewModel.selectProcess(proc) }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(GlassBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Terminal, // Using terminal to represent back/foreground unified
                                                contentDescription = null,
                                                tint = LocalAccentColor.current,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(proc.name, color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Background Service • PID ${proc.pid}", color = MutedGrey, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        Text("${proc.currentCpu.toInt()}%", color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }

                                // Show All Button
                                TextButton(
                                    onClick = { viewModel.navigateTo(Screen.LiveMonitor) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Show All Active Processes", color = LocalAccentColor.current, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = "View more", tint = LocalAccentColor.current, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PowerSaverToggle() {
    var powerSaverEnabled by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LocalAccentColor.current.copy(alpha = 0.1f))
            .border(BorderStroke(1.dp, LocalAccentColor.current.copy(alpha = 0.3f)), RoundedCornerShape(16.dp))
            .clickable { powerSaverEnabled = !powerSaverEnabled }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LocalAccentColor.current.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null, tint = LocalAccentColor.current)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Power Saver", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = if (powerSaverEnabled) "Active - Throttling background processes" else "Inactive - Tap to enable",
                    color = MutedGrey,
                    fontSize = 12.sp
                )
            }
            Switch(
                checked = powerSaverEnabled,
                onCheckedChange = { powerSaverEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = LocalAccentColor.current,
                    checkedTrackColor = LocalAccentColor.current.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun SystemDashboardScreen(viewModel: MainViewModel) {
    val sysCpu by viewModel.sysCpu.collectAsStateWithLifecycle()
    val sysMemory by viewModel.sysMemory.collectAsStateWithLifecycle()
    val sysBattery by viewModel.sysBattery.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val procs by viewModel.processes.collectAsStateWithLifecycle()
    
    var isOptimizingConfig by remember { mutableStateOf(false) }
    var optimizedConfig by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val memInfoText = remember(sysMemory) {
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val mi = android.app.ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            val totalGB = String.format("%.1f", mi.totalMem.toDouble() / (1024 * 1024 * 1024))
            val usedGB = String.format("%.1f", (mi.totalMem - mi.availMem).toDouble() / (1024 * 1024 * 1024))
            "$usedGB GB / $totalGB GB Active Alloc"
        } catch (e: Exception) {
            "Memory load status active"
        }
    }

    val batteryInfoText = remember(sysBattery) {
        try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryIntent = context.registerReceiver(null, filter)
            val voltage = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
            val temp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
            val voltageVolts = if (voltage > 0) String.format("%.2f", voltage / 1000.0) + "V" else "Unknown"
            val tempCelsius = if (temp > 0) "${temp / 10.0}°C" else "Unknown"
            "Voltage: $voltageVolts • Temp: $tempCelsius"
        } catch (e: Exception) {
            "Voltage: Unknown • Temp: Unknown"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            DashboardHeader(viewModel = viewModel)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Bento-Style layout
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // CPU summary card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("CPU Usage", color = MutedGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("$sysCpu%", color = NeonBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { (sysCpu / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                color = NeonBlue,
                                trackColor = CoreBorder
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${procs.size} telemetry nodes tracked", color = MutedGrey, fontSize = 10.sp)
                        }
                    }

                    // RAM summary card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Memory", color = MutedGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("$sysMemory%", color = NeonBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { (sysMemory / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                color = NeonBlue,
                                trackColor = CoreBorder
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(memInfoText, color = MutedGrey, fontSize = 10.sp)
                        }
                    }

                    // Battery summary card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Battery", color = MutedGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("$sysBattery%", color = NeonBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { (sysBattery / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                color = NeonBlue,
                                trackColor = CoreBorder
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(batteryInfoText, color = MutedGrey, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Dynamic High Resource Usage alert based on real proc data
                val maxRamProc = procs.maxByOrNull { it.currentRam }
                val showResourceAlert = maxRamProc != null && maxRamProc.currentRam > 250.0 // Threshold in MB
                
                if (showResourceAlert && maxRamProc != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("optimize_alert_banner"),
                        colors = CardDefaults.cardColors(containerColor = CoreSurface),
                        border = BorderStroke(1.dp, CoreBorder),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Warning, null, tint = CyberRed, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("High Resource Usage", color = CoreText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${maxRamProc.name} is consuming ${maxRamProc.currentRam.toInt()} MB of RAM.", color = MutedText, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            AnimatedContent(
                                targetState = when {
                                    optimizedConfig -> 2
                                    isOptimizingConfig -> 1
                                    else -> 0
                                },
                                label = "optimize_btn_config",
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                                }
                            ) { state ->
                                when (state) {
                                    2 -> Icon(Icons.Default.Check, contentDescription = "Optimized", tint = LocalAccentColor.current, modifier = Modifier.size(24.dp))
                                    1 -> CircularProgressIndicator(modifier = Modifier.size(20.dp), color = LocalAccentColor.current, strokeWidth = 2.dp)
                                    else -> TextButton(
                                        onClick = {
                                            isOptimizingConfig = true
                                            scope.launch {
                                                delay(1200)
                                                isOptimizingConfig = false
                                                optimizedConfig = true
                                                delay(1000)
                                                viewModel.killProcess(context, maxRamProc.pid)
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        modifier = Modifier.height(32.dp).border(1.dp, CoreBorder, RoundedCornerShape(16.dp))
                                    ) {
                                        Text("Optimize", color = LocalAccentColor.current, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Top Applications
                Text(
                    text = "Top Applications",
                    color = LightGrey,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        if (procs.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Awaiting Telemetry...", color = MutedGrey, fontSize = 13.sp)
                            }
                        } else {
                            val topApps = procs.sortedByDescending { it.currentCpu }.take(4)
                            topApps.forEachIndexed { idx, proc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectProcess(proc) }
                                        .padding(vertical = 12.dp, horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NeonBlue.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when(proc.category) {
                                                "System" -> Icons.Default.Terminal
                                                else -> Icons.Default.Apps
                                            },
                                            contentDescription = null,
                                            tint = NeonBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(proc.name, color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Package • ${proc.pid.take(18)}...", color = MutedGrey, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text("${proc.currentCpu.toInt()}% Load", color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Network Traffic Chart Visualizer
                Text("Network Traffic", color = LightGrey, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val dlSpeed by viewModel.downloadSpeed.collectAsStateWithLifecycle()
                        val ulSpeed by viewModel.uploadSpeed.collectAsStateWithLifecycle()
                        val netHistory by viewModel.netHistory.collectAsStateWithLifecycle()

                        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                            if (netHistory.isEmpty()) return@Canvas
                            // Render dynamic real bar graphs scaling live throughput speeds
                            val maxHist = (netHistory.filterNot { it.isNaN() || it.isInfinite() }.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
                            val barWidth = size.width / (netHistory.size * 2f).coerceAtLeast(1f)
                            netHistory.forEachIndexed { index, valD ->
                                val safeValD = if (valD.isNaN() || valD.isInfinite()) 0.0 else valD
                                val h = (safeValD / maxHist).toFloat().coerceIn(0.1f, 1f)
                                drawRect(
                                    brush = Brush.verticalGradient(listOf(NeonBlue, NeonBlue.copy(alpha=0.6f))),
                                    topLeft = Offset(index * barWidth * 2f + barWidth / 2f, size.height * (1f - h)),
                                    size = Size(barWidth, size.height * h)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("LIVE FEED", color = Color.Gray, fontSize = 10.sp)
                            Text("Upload: ${String.format("%.1f", ulSpeed)} KB/s", color = NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Download: ${String.format("%.1f", dlSpeed)} KB/s", color = NeonBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("SYNC ACTIVE", color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // System Telemetry Access Control Card
                Text("System Telemetry Access Settings", color = LightGrey, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                var usageAccess by remember { mutableStateOf(viewModel.hasUsageStatsPermission()) }
                var batteryAccess by remember { mutableStateOf(viewModel.hasBatteryOptimizationsIgnorePermission(context)) }
                var networkAccess by remember { mutableStateOf(viewModel.hasNetworkPermission(context)) }

                DisposableEffect(Unit) {
                    val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                        if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                            usageAccess = viewModel.hasUsageStatsPermission()
                            batteryAccess = viewModel.hasBatteryOptimizationsIgnorePermission(context)
                            networkAccess = viewModel.hasNetworkPermission(context)
                        }
                    }
                    val lifecycle = (context as? androidx.activity.ComponentActivity)?.lifecycle
                    lifecycle?.addObserver(observer)
                    onDispose {
                        lifecycle?.removeObserver(observer)
                    }
                }

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Monitor, null, tint = if (usageAccess) NeonBlue else MutedGrey, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Usage Telemetry", color = LightGrey, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(if (usageAccess) "Access Granted" else "Awaiting Configuration", color = if (usageAccess) GreenSync else CyberRed, fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    try {
                                        val intent = android.content.Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(context, "Settings not available", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (usageAccess) Color.Transparent else NeonBlue),
                                border = if (usageAccess) BorderStroke(1.dp, NeonBlue.copy(alpha = 0.4f)) else null,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(if (usageAccess) "Configure" else "Enable", fontSize = 11.sp, color = if (usageAccess) NeonBlue else Color.White)
                            }
                        }

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SurfaceContainerHigh))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.BatteryChargingFull, null, tint = if (batteryAccess) NeonBlue else MutedGrey, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Battery Analytics (PWR)", color = LightGrey, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(if (batteryAccess) "Unlimited Access" else "Power Throttled", color = if (batteryAccess) GreenSync else CyberRed, fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    try {
                                        val intent = android.content.Intent(
                                            android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                        ).apply {
                                            data = android.net.Uri.parse("package:${context.packageName}")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        try {
                                            val intent = android.content.Intent(
                                                android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                                            )
                                            context.startActivity(intent)
                                        } catch (e2: Exception) {
                                            android.widget.Toast.makeText(context, "Settings not available", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (batteryAccess) Color.Transparent else NeonBlue),
                                border = if (batteryAccess) BorderStroke(1.dp, NeonBlue.copy(alpha = 0.4f)) else null,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(if (batteryAccess) "Configure" else "Enable", fontSize = 11.sp, color = if (batteryAccess) NeonBlue else Color.White)
                            }
                        }

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SurfaceContainerHigh))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NetworkCheck, null, tint = if (networkAccess) NeonBlue else MutedGrey, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Network Analytics", color = LightGrey, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(if (networkAccess) "Monitoring Active" else "Link Offline", color = if (networkAccess) GreenSync else CyberRed, fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    try {
                                        val intent = android.content.Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(context, "Settings not available", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (networkAccess) Color.Transparent else NeonBlue),
                                border = if (networkAccess) BorderStroke(1.dp, NeonBlue.copy(alpha = 0.4f)) else null,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(if (networkAccess) "Configure" else "Enable", fontSize = 11.sp, color = if (networkAccess) NeonBlue else Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
}
}
@Composable
fun LiveMonitorScreen(viewModel: MainViewModel) {
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
    val procs by viewModel.processes.collectAsStateWithLifecycle()
    val sysCpu by viewModel.sysCpu.collectAsStateWithLifecycle()
    val sysMemory by viewModel.sysMemory.collectAsStateWithLifecycle()
    val sysBattery by viewModel.sysBattery.collectAsStateWithLifecycle()
    val netDownload by viewModel.downloadSpeed.collectAsStateWithLifecycle()

    val filteredProcs = remember(activeFilter, procs) {
        when(activeFilter) {
            "CPU" -> procs.sortedByDescending { it.currentCpu }
            "RAM" -> procs.sortedByDescending { it.currentRam }
            "Battery" -> procs.sortedByDescending { it.lastUsed } // Fallback for battery since we can't get per-app battery without root
            "Network" -> procs.sortedByDescending { it.currentRam } // Assuming currentRam stores mapped network usage if added via Fallback
            else -> procs
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            DashboardHeader(viewModel = viewModel)

            // System Metrics Overview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val safeNet = if (netDownload.isNaN() || netDownload.isInfinite()) 0.0 else netDownload
                val netString = "${(safeNet * 10).toInt() / 10.0}KB"
                MetricSmallCard(title = "CPU", value = "$sysCpu%", icon = Icons.Default.Memory, color = NeonBlue)
                MetricSmallCard(title = "RAM", value = "$sysMemory%", icon = Icons.Default.SdStorage, color = NeonBlue)
                MetricSmallCard(title = "BAT", value = "$sysBattery%", icon = Icons.Default.BatteryFull, color = NeonBlue)
                MetricSmallCard(title = "NET", value = netString, icon = Icons.Default.Wifi, color = NeonBlue)
            }

            // Filter Chips Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("CPU", "RAM", "Battery", "Network")
                filters.forEach { filter ->
                    val isSelected = filter == activeFilter
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) NeonBlue else GlassBg)
                            .clickable { viewModel.setFilter(filter) }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("filter_chip_$filter"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else LightGrey,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Live app monitor list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                items(filteredProcs) { proc ->
                    val isNeuralKernel = proc.name.contains("Neural")
                    val isCritical = proc.status.contains("SPIKE")
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(if (isNeuralKernel) "neural_kernel_card" else "live_process_item")
                            .clickable {
                                viewModel.selectProcess(proc)
                            },
                        border = BorderStroke(1.dp, if (isCritical) CyberRed else SurfaceContainerHigh)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCritical) CyberRed.copy(alpha=0.15f) else LocalAccentColor.current.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (proc.category == "System") Icons.Default.Terminal else Icons.Default.Apps,
                                    contentDescription = null,
                                    tint = if (isCritical) CyberRed else NeonBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(proc.name, color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Process ID: ${proc.pid}", color = MutedGrey, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${proc.currentCpu.toInt()}%", color = if (isCritical) CyberRed else NeonBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(48.dp, 4.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHigh)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth((if (proc.currentCpu.isNaN() || proc.currentCpu.isInfinite()) 0f else proc.currentCpu.toFloat() / 100f).coerceIn(0f, 1f))
                                            .background(if (isCritical) CyberRed else NeonBlue)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Current Load indicator card
        val cpuLoad by viewModel.cpuLoad.collectAsStateWithLifecycle()
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 92.dp, end = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(GlassBg)
                .border(BorderStroke(1.dp, NeonBlue), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "pulse")
            val isHighLoad = cpuLoad > 50
            val targetColor = if (isHighLoad) CyberRed else NeonBlue
            
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                    animation = androidx.compose.animation.core.tween(durationMillis = 2500, easing = androidx.compose.animation.core.LinearEasing),
                    repeatMode = androidx.compose.animation.core.RepeatMode.Restart
                ),
                label = "rotation"
            )
            
            val pulse by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                    animation = androidx.compose.animation.core.tween(durationMillis = 1500, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                ),
                label = "pulse"
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(52.dp)) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().graphicsLayer(rotationZ = rotation, scaleX = pulse, scaleY = pulse)) {
                        drawArc(
                            color = targetColor.copy(alpha = 0.6f),
                            startAngle = 0f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )
                    }
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(8.dp).graphicsLayer(rotationZ = -rotation * 1.5f)) {
                        drawArc(
                            color = targetColor.copy(alpha = 0.8f),
                            startAngle = 45f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 2.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = "System Load",
                        tint = targetColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Core Load", color = MutedGrey, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(if (cpuLoad < 0) "..." else "$cpuLoad%", color = targetColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

    }
}

@Composable
fun AppDetailViewScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val selectedProcState by viewModel.selectedProcess.collectAsStateWithLifecycle()
    val processes by viewModel.processes.collectAsStateWithLifecycle()
    val proc = selectedProcState ?: processes.firstOrNull() ?: ProcessInfo(
        pid = "com.android.system",
        name = "CORE_OS System Console",
        initialCpu = 4.5,
        initialRam = 112.0,
        status = "Active",
        category = "CPU"
    )

    // Collect dynamic process statistics updates
    val cpuHistory = remember(proc.pid) { mutableStateListOf<Float>() }
    val ramHistory = remember(proc.pid) { mutableStateListOf<Float>() }

    LaunchedEffect(proc.currentCpu) {
        if (cpuHistory.size >= 15) {
            cpuHistory.removeAt(0)
        }
        cpuHistory.add(proc.currentCpu.toFloat())
    }

    LaunchedEffect(proc.currentRam) {
        if (ramHistory.size >= 15) {
            ramHistory.removeAt(0)
        }
        ramHistory.add(proc.currentRam.toFloat())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "arrow_back",
                        tint = NeonBlue,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { viewModel.navigateBack() }
                            .testTag("app_detail_back_btn")
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "CORE_OS DIAGNOSTICS",
                        color = NeonBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                }

                // Profile Image Placeholder
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LocalAccentColor.current.copy(alpha=0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = NeonBlue, modifier = Modifier.size(20.dp))
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(100.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(GlassBg)
                                    .border(BorderStroke(2.dp, NeonBlue), RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when(proc.category) {
                                        "System" -> Icons.Default.Terminal
                                        else -> Icons.Default.Apps
                                    },
                                    null,
                                    tint = NeonBlue,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NeonBlue)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("SYSTEM", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            proc.name,
                            color = LightGrey,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "Package ID: ${proc.pid} • Priority: Telemetry Core",
                            color = MutedGrey,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                // CPU usage card
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Relative Usage Timeline", color = MutedGrey, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${String.format("%.1f", proc.currentCpu)}%", color = NeonBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                                val cleanCpu = cpuHistory.map { if (it.isNaN() || it.isInfinite()) 0f else it }
                                if (cleanCpu.size > 1) {
                                    val path = Path()
                                    val stepX = size.width / (cleanCpu.size - 1)
                                    cleanCpu.forEachIndexed { index, value ->
                                        val x = index * stepX
                                        val y = size.height * (1f - (value / 50f).coerceIn(0.05f, 0.95f))
                                        if (index == 0) {
                                            path.moveTo(x, y)
                                        } else {
                                            path.lineTo(x, y)
                                        }
                                    }
                                    drawPath(
                                        path = path,
                                        color = NeonBlue,
                                        style = Stroke(5f)
                                    )
                                } else {
                                    drawLine(
                                        color = NeonBlue.copy(alpha = 0.3f),
                                        start = Offset(0f, size.height * 0.7f),
                                        end = Offset(size.width, size.height * 0.7f),
                                        strokeWidth = 4f
                                    )
                                }
                            }
                        }
                    }
                }

                // RAM usage card
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Network/Memory Load", color = MutedGrey, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${proc.currentRam.toInt()} MB", color = NeonBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                                val cleanRam = ramHistory.map { if (it.isNaN() || it.isInfinite()) 0f else it }
                                if (cleanRam.size > 1) {
                                    val path = Path()
                                    val stepX = size.width / (cleanRam.size - 1)
                                    cleanRam.forEachIndexed { index, value ->
                                        val x = index * stepX
                                        val y = size.height * (1f - (value / 600f).coerceIn(0.05f, 0.95f))
                                        if (index == 0) {
                                            path.moveTo(x, y)
                                        } else {
                                            path.lineTo(x, y)
                                        }
                                    }
                                    drawPath(
                                        path = path,
                                        color = NeonBlue,
                                        style = Stroke(5f)
                                    )
                                } else {
                                    drawLine(
                                        color = NeonBlue.copy(alpha = 0.3f),
                                        start = Offset(0f, size.height * 0.5f),
                                        end = Offset(size.width, size.height * 0.5f),
                                        strokeWidth = 4f
                                    )
                                }
                            }
                        }
                    }
                }

                // Power consumption overview
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, SurfaceContainerHigh)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NeonBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.BatteryChargingFull, null, tint = NeonBlue, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Device Power Optimization", color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Telemetry monitor is actively tracking scheduling interrupts to prevent CPU spike optimizations.", color = MutedGrey, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // Restrict Activity, Optimize buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.killProcess(context, proc.pid)
                                viewModel.navigateBack()
                            },
                            modifier = Modifier.weight(1f).height(52.dp),
                            border = BorderStroke(1.dp, SurfaceContainerHigh),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Restrict Activity", color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.purgeCache()
                                viewModel.navigateBack()
                            },
                            modifier = Modifier.weight(1.5f).height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Optimize Process", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsPreviewScreen(viewModel: MainViewModel) {
    val rawCpuHistory by viewModel.cpuHistory.collectAsStateWithLifecycle()
    val rawPwrHistory by viewModel.pwrHistory.collectAsStateWithLifecycle()
    val rawNetHistory by viewModel.netHistory.collectAsStateWithLifecycle()

    val cpuHistory = remember(rawCpuHistory) { rawCpuHistory.map { it.toFloat() } }
    val pwrHistory = remember(rawPwrHistory) { rawPwrHistory.map { it.toFloat() } }
    val netHistory = remember(rawNetHistory) { rawNetHistory.map { it.toFloat() } }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            DashboardHeader(viewModel = viewModel)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                item {
                    Text(
                        text = "REALTIME TELEMETRY HISTORY",
                        color = MutedGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pinch to zoom, drag to pan within charts.",
                        color = MutedGrey,
                        fontSize = 12.sp
                    )
                }

                item { TelemetryChart(title = "CPU Usage (%)", dataPoints = cpuHistory, color = CyberRed) }
                item { TelemetryChart(title = "Battery Level (%)", dataPoints = pwrHistory, color = LocalAccentColor.current) }
                item { TelemetryChart(title = "Network Activity (KB/s)", dataPoints = netHistory, color = LocalAccentColor.current) }
            }
        }
    }
}

@Composable
fun TelemetryChart(title: String, dataPoints: List<Float>, color: Color) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ShowChart, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 5f)
                        // Limit panning so it doesn't go entirely off screen
                        val maxPan = 500f * scale
                        offsetX = (offsetX + pan.x * scale).coerceIn(-maxPan, maxPan)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = scale, translationX = offsetX)) {
                val cleanData = dataPoints.map { if (it.isNaN() || it.isInfinite()) 0f else it }
                if (cleanData.size < 2) return@Canvas
                val width = size.width
                val height = size.height
                val minVal = cleanData.minOrNull() ?: 0f
                val maxVal = cleanData.maxOrNull() ?: 1f
                val range = (maxVal - minVal).coerceAtLeast(0.1f)
                val spacing = width / (cleanData.size - 1)

                val path = androidx.compose.ui.graphics.Path()
                val bottomOffset = 4.dp.toPx()
                val effectiveHeight = height - bottomOffset * 2
                path.moveTo(0f, bottomOffset + effectiveHeight - ((cleanData[0] - minVal) / range) * effectiveHeight)

                for (i in 1 until cleanData.size) {
                    val x = i * spacing
                    val y = bottomOffset + effectiveHeight - ((cleanData[i] - minVal) / range) * effectiveHeight
                    path.lineTo(x, y)
                }

                drawPath(
                    path = path,
                    color = color,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )

                // Fill gradient
                val fillPath = androidx.compose.ui.graphics.Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(color.copy(alpha = 0.2f), Color.Transparent)
                    )
                )
            }
        }
    }
}


