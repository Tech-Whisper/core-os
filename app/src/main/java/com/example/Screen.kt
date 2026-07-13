package com.example

sealed class Screen(val route: String) {
    object OnboardingIntro : Screen("onboarding_intro")
    object OnboardingFeatures : Screen("onboarding_features")
    object OnboardingPermissions : Screen("onboarding_permissions")
    object HomeDashboard : Screen("home_dashboard")
    object SystemDashboard : Screen("system_dashboard")
    object LiveMonitor : Screen("live_monitor")
    object AppDetailView : Screen("app_detail")
    object NotificationsPreview : Screen("notifications_preview")
}
