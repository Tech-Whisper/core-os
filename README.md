# CORE_OS — Next-Gen System Intelligence

CORE_OS is a high-performance, responsive system monitoring and device optimization application for Android. Built entirely with modern **Kotlin**, **Jetpack Compose**, and **Material Design 3 (M3)**, it delivers real-time system metrics, active process diagnostics, and system optimization utilities in a highly polished, interactive interface.

---

## Features

- **Real-Time Device Health Score**: A simple, intuitive health indicator (e.g., `90/100`) calculated from active CPU load, memory pressure, and thermal metrics.
- **One-Tap Quick Optimizers**:
  - **Cool Down**: Triggers a memory cleanup and reduces CPU pressure by safely stopping background processes.
  - **Battery Optimize**: Evaluates battery drain rates, charging status, and applies localized performance rules to conserve power.
  - **Network Scan**: Scans active socket dependencies and monitors network latency.
- **Performance Diagnostics & Alerts**: Automatically identifies the specific application or background service causing high CPU spikes.
- **Deep Dive Analytics**: High-fidelity, real-time scrolling charts tracking system performance over time.

---

## Built With

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- **Architecture**: MVVM with Clean Architecture patterns
- **State Management**: `StateFlow` and structured Coroutine streams
- **System Telemetry**: Low-overhead parsing of native `/proc` and `BatteryManager` files
- **Optimization**: ProGuard/R8, resource stripping (en only), ABI filtering (arm64-v8a, armeabi-v7a)

---

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) Ladybug or newer
- JDK 17+
- Android SDK 36

### 1. Clone

```bash
git clone https://github.com/<your-username>/core-os.git
cd core-os
```

### 2. Set up API keys (optional)

```bash
cp .env.example .env
```

Edit `.env` and replace `MY_GEMINI_API_KEY` with your actual Gemini API key from [Google AI Studio](https://aistudio.google.com/).

### 3. Set up signing (for release builds)

The following environment variables are required for a signed release build:

| Variable | Description |
|----------|-------------|
| `KEYSTORE_PATH` | Path to your release keystore |
| `STORE_PASSWORD` | Keystore password |
| `KEY_PASSWORD` | Key password |

For debug builds, the following env vars can be used (falls back to well-known debug defaults):

| Variable | Description |
|----------|-------------|
| `DEBUG_KEYSTORE_PATH` | Path to debug keystore |
| `DEBUG_STORE_PASSWORD` | Debug keystore password |
| `DEBUG_KEY_ALIAS` | Debug key alias |
| `DEBUG_KEY_PASSWORD` | Debug key password |

### 4. Build

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (requires signing env vars)
./gradlew assembleRelease
```

### 5. Run Tests

```bash
./gradlew :app:testDebugUnitTest
```

---

## Project Structure

```
/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── MainViewModel.kt
│   │   │   │   ├── CpuTracker.kt
│   │   │   │   ├── DataRepository.kt
│   │   │   │   ├── DashboardScreens.kt
│   │   │   │   ├── OnboardingScreens.kt
│   │   │   │   ├── MetricsService.kt
│   │   │   │   ├── Screen.kt
│   │   │   │   └── ui/theme/
│   │   │   └── AndroidManifest.xml
│   │   └── proguard-rules.pro
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Security

- No secrets are hardcoded in source code. All API keys are loaded from `.env` via the Google Secrets Gradle Plugin.
- Signing credentials use `System.getenv()` with secure fallbacks for local development only.
- OkHttp logging interceptor is included — ensure `HttpLoggingInterceptor.Level` is set to `NONE` or `BASIC` in production builds.
- ProGuard/R8 is enabled for release builds with full obfuscation and shrinking.

### Permissions

The app requests several Android permissions for system monitoring:

- `PACKAGE_USAGE_STATS` — to read app usage statistics
- `QUERY_ALL_PACKAGES` — to list installed applications
- `KILL_BACKGROUND_PROCESSES` — to stop misbehaving apps
- `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_DATA_SYNC` — for background metric collection
- `POST_NOTIFICATIONS` — for foreground service notification
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` — for battery optimization features

> **Note**: `QUERY_ALL_PACKAGES` may require justification for Google Play Store compliance. Verify necessity before publishing.

---

## License

This project is provided for educational and demonstration purposes.
