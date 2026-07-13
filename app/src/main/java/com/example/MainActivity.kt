package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.tween
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.CompositionLocalProvider
import com.example.ui.theme.NeonBlue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                
                CompositionLocalProvider(LocalAccentColor provides NeonBlue) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = com.example.ui.theme.DarkVoid,
                        bottomBar = {
                            if (currentScreen is Screen.HomeDashboard || 
                                currentScreen is Screen.SystemDashboard || 
                                currentScreen is Screen.LiveMonitor ||
                                currentScreen is Screen.AppDetailView) {
                                SystemBottomNavigation(
                                    currentScreen = currentScreen,
                                    onTabSelected = { screen -> viewModel.navigateTab(screen) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentScreen,
                                transitionSpec = {
                                    // Premium staggered transitions
                                    fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                                },
                                label = "screen_transition"
                            ) { screen ->
                                when (screen) {
                                    is Screen.OnboardingIntro -> OnboardingIntroScreen(viewModel = viewModel)
                                    is Screen.OnboardingFeatures -> OnboardingFeaturesScreen(viewModel = viewModel)
                                    is Screen.OnboardingPermissions -> OnboardingPermissionsScreen(viewModel = viewModel)
                                    is Screen.HomeDashboard -> HomeDashboardScreen(viewModel = viewModel)
                                    is Screen.SystemDashboard -> SystemDashboardScreen(viewModel = viewModel)
                                    is Screen.LiveMonitor -> LiveMonitorScreen(viewModel = viewModel)
                                    is Screen.AppDetailView -> AppDetailViewScreen(viewModel = viewModel)
                                    is Screen.NotificationsPreview -> NotificationsPreviewScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
