# CORE_OS — Agent Context

## Project Overview
Android system monitoring app built with Kotlin, Jetpack Compose, Material Design 3.

## Build Commands
- Build debug APK: `./gradlew assembleDebug`
- Run unit tests: `./gradlew :app:testDebugUnitTest`
- Run instrumented tests: `./gradlew :app:connectedAndroidTest`
- Run lint: `./gradlew lint`

## Architecture
- MVVM with Clean Architecture
- ViewModel: `MainViewModel` manages all UI state via `StateFlow`
- Navigation: custom sealed class `Screen` with `Stack`-based nav
- Theme: Custom Material3 dark theme in `ui/theme/`

## Key Dependencies
- Jetpack Compose + Material3
- OkHttp + Retrofit + Moshi (networking)
- Room (database, configured but usage TBD)
- Coil (image loading)
- Accompanist Permissions
- Roborazzi (screenshot testing)

## Secrets Management
- No API keys or `.env` files are used — the app has no remote secrets.
- Release signing: env vars `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`
- Debug signing: env vars `DEBUG_KEYSTORE_PATH`, `DEBUG_STORE_PASSWORD`, `DEBUG_KEY_ALIAS`, `DEBUG_KEY_PASSWORD`

## Security Notes
- No hardcoded secrets in source.
- OkHttp logging interceptor included — ensure `HttpLoggingInterceptor.Level` set to `NONE` or `BASIC` in production
- ProGuard/R8 enabled for release builds
- Debug signing credentials in `app/build.gradle.kts` use env vars with secure defaults only for local dev
- `QUERY_ALL_PACKAGES` permission used — verify necessity for Google Play Store compliance
