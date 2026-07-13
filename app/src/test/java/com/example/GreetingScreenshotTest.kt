package com.example

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent { MyApplicationTheme { Text("CORE OS") } }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun test_screens_navigation() {
    composeTestRule.setContent {
      MyApplicationTheme {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val viewModel = MainViewModel(application = context as android.app.Application)
        OnboardingIntroScreen(viewModel = viewModel)
        OnboardingFeaturesScreen(viewModel = viewModel)
        OnboardingPermissionsScreen(viewModel = viewModel)
        HomeDashboardScreen(viewModel = viewModel)
        SystemDashboardScreen(viewModel = viewModel)
        LiveMonitorScreen(viewModel = viewModel)
        AppDetailViewScreen(viewModel = viewModel)
        NotificationsPreviewScreen(viewModel = viewModel)
      }
    }
  }
}
