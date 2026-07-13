package com.example

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    border: BorderStroke = BorderStroke(1.dp, SurfaceContainerHigh),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GlassBg),
        shape = RoundedCornerShape(24.dp),
        border = border,
        content = content
    )
}

@Composable
fun AmbientCoreBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkVoid)
            .drawBehind {
                // Subtle radial light indigo glow matching professional theme
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x1F4F46E5), Color.Transparent),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.width * 0.8f
                    )
                )
            }
    ) {
        content()
    }
}

@Composable
fun OnboardingIntroScreen(viewModel: MainViewModel) {
    AmbientCoreBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(GlassBg)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Hexagon,
                        contentDescription = "Logo",
                        tint = NeonBlue,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "CORE_OS",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "The ultimate system intelligence and diagnostics engine. Monitor, optimize, and secure your device with real-time telemetry.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        color = MutedGrey,
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Navigation Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.navigateTo(Screen.OnboardingFeatures) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag("next_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Get Started",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Forward Icon",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = {
                        viewModel.completeOnboarding()
                        viewModel.navigateTo(Screen.HomeDashboard)
                    },
                    modifier = Modifier.testTag("skip_documentation_btn")
                ) {
                    Text(
                        text = "Skip to Dashboard",
                        color = MutedGrey,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingFeaturesScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentBattery = remember {
        val bm = context.getSystemService(android.content.Context.BATTERY_SERVICE) as android.os.BatteryManager
        bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(1..100)
    }
    val dlSpeed by viewModel.downloadSpeed.collectAsStateWithLifecycle()
    val ulSpeed by viewModel.uploadSpeed.collectAsStateWithLifecycle()
    AmbientCoreBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.GridView,
                        contentDescription = "Grid Icon",
                        tint = NeonBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CORE_OS",
                        color = NeonBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                // Profile Ring
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(NeonBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Mock",
                        tint = NeonBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Section title
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NeonBlue.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonBlue))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FEATURE OVERVIEW",
                            color = NeonBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Power and Control",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Explore the advanced telemetry suite designed to provide granular insights into your system's neural pathways and resource allocation.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MutedGrey,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bento Grid elements
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Feature item 1
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShowChart,
                                    contentDescription = "Stats",
                                    tint = NeonBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Real-time Stats",
                                color = LightGrey,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Millisecond-accurate tracking of CPU frequency, thread load, and memory architecture performance.",
                                color = MutedGrey,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(3.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHigh)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(0.66f)
                                            .background(Brush.horizontalGradient(listOf(NeonBlue, NeonBlue)))
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("LIVE_FEED", color = NeonBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Feature item 2
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Apps,
                                    contentDescription = "Apps",
                                    tint = NeonBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Per-app Usage",
                                color = LightGrey,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Deep inspection of individual application footprints and kernel-level resource requests.",
                                color = MutedGrey,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val labels = listOf("BROWSER", "IDE", "KERNEL")
                                labels.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SurfaceContainer)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(tag, color = MutedGrey, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Feature item 3
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BatteryChargingFull,
                                    contentDescription = "Battery",
                                    tint = NeonBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Battery Health",
                                color = LightGrey,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Predictive degradation modeling and peak voltage stability monitoring for cellular longevity.",
                                color = MutedGrey,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("$currentBattery", color = NeonBlue, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PERCENT_CAPACITY", color = MutedGrey, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Feature item 4
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NetworkCheck,
                                    contentDescription = "Network",
                                    tint = NeonBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Network Traffic",
                                color = LightGrey,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Encrypted packet inspection and throughput visualization across all active interfaces.",
                                color = MutedGrey,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("↑ ${String.format("%.1f", ulSpeed)} KB/s", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("↓ ${String.format("%.1f", dlSpeed)} KB/s", color = NeonBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.OnboardingPermissions) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("initialize_core_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Text(
                            stringResource(R.string.initialize_core),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(Screen.NotificationsPreview) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("system_diagnostics_button"),
                        border = BorderStroke(1.dp, NeonBlue.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Text(
                            stringResource(R.string.system_diagnostics),
                            color = NeonBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Nav items representing Dashboard flow (nav bar mockup)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(GlassBg),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { viewModel.navigateTo(Screen.HomeDashboard) }
                        .testTag("nav_dashboard_box"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Dashboard, "dashboard", tint = MutedGrey, modifier = Modifier.size(20.dp))
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { viewModel.navigateTo(Screen.LiveMonitor) }
                        .testTag("nav_monitoring_box"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Monitor, "monitoring", tint = MutedGrey, modifier = Modifier.size(20.dp))
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { viewModel.navigateTo(Screen.NotificationsPreview) }
                        .testTag("nav_history_box"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.History, "history", tint = MutedGrey, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun OnboardingPermissionsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var usageAccess by remember { mutableStateOf(viewModel.hasUsageStatsPermission()) }
    var batteryAccess by remember { mutableStateOf(viewModel.hasBatteryOptimizationsIgnorePermission(context)) }
    var networkAccess by remember { mutableStateOf(viewModel.hasNetworkPermission(context)) }

    // Refresh permission when screen is Resumed
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

    AmbientCoreBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GlassBg)
                        .clickable { viewModel.navigateTo(Screen.OnboardingIntro) }
                        .testTag("grid_view_back_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.GridView,
                        contentDescription = "grid_view",
                        tint = NeonBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Step 03 / 04",
                    color = MutedGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.required_access),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.permissions_body),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MutedGrey,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Permissions toggle list
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Usage Access Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Monitor, "Usage", tint = NeonBlue, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(R.string.usage_access), color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(stringResource(R.string.usage_access_desc), color = MutedGrey, fontSize = 12.sp)
                            }
                            Switch(
                                checked = usageAccess,
                                onCheckedChange = {
                                    if (it) {
                                        val intent = android.content.Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
                                        context.startActivity(intent)
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonBlue)
                            )
                        }
                    }

                    // Battery Stats / PWR Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.BatteryChargingFull, "Power", tint = NeonBlue, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(R.string.battery_stats), color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(stringResource(R.string.battery_stats_desc), color = MutedGrey, fontSize = 12.sp)
                            }
                            Switch(
                                checked = batteryAccess,
                                onCheckedChange = {
                                    if (it) {
                                        try {
                                            val intent = android.content.Intent(
                                                android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                            ).apply {
                                                data = android.net.Uri.parse("package:${context.packageName}")
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            val intent = android.content.Intent(
                                                android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                                            )
                                            context.startActivity(intent)
                                        }
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonBlue)
                            )
                        }
                    }

                    // Network Configuration Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.NetworkCheck, "Network", tint = NeonBlue, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(R.string.network_access), color = LightGrey, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(stringResource(R.string.network_access_desc), color = MutedGrey, fontSize = 12.sp)
                            }
                            Switch(
                                checked = networkAccess,
                                onCheckedChange = {
                                    if (it) {
                                        val intent = android.content.Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                                        context.startActivity(intent)
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonBlue)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Encryption notification detail container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainer, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonBlue))
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonBlue.copy(alpha = 0.25f)))
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonBlue.copy(alpha = 0.25f)))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.encryption_active).uppercase(),
                            color = MutedGrey,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }

            // Footer / CTA
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        viewModel.completeOnboarding()
                        viewModel.navigateTo(Screen.HomeDashboard)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("grant_all_permissions_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.grant_all_permissions),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Forward Icon",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "BY PROCEEDING, YOU AGREE TO OUR SYSTEM INTEGRITY PROTOCOLS",
                    color = MutedGrey,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
