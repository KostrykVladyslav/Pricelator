# Pricelator - Currency Conversion Android App

## Project Overview

**Pricelator** is a modern Android application that allows users to instantly scan price tags using their camera and convert prices to their desired currency in real-time. It's designed as a travel companion for shopping abroad, providing quick and accurate currency conversions through optical character recognition (OCR).

**Version**: 1.1.1 (Build 7)
**Package**: com.kostryk.pricelator
**Min SDK**: 29 (Android 10)
**Target SDK**: 36

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Module Structure](#module-structure)
3. [Key Features](#key-features)
4. [Technology Stack](#technology-stack)
5. [Project Structure](#project-structure)
6. [Navigation Architecture](#navigation-architecture)
7. [Data Flow](#data-flow)
8. [Build Configuration](#build-configuration)
9. [Getting Started](#getting-started)

---

## Architecture Overview

The project follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────────────────┐
│              Presentation Layer                      │
│     (Feature Modules - Jetpack Compose)             │
├─────────────────────────────────────────────────────┤
│              Domain Layer                            │
│     (Business Logic, Entities, Use Cases)           │
├─────────────────────────────────────────────────────┤
│              Data Layer                              │
│     (Repository Impl, Room, Retrofit)               │
└─────────────────────────────────────────────────────┘
```

### Design Patterns

- **MVVM (Model-View-ViewModel)** - UI state management with StateFlow
- **Repository Pattern** - Abstraction between data sources and business logic
- **Use Case Pattern** - Encapsulation of single-responsibility business operations
- **Factory Pattern** - Modular navigation via `NavigationFactory` interface
- **Manager Pattern** - Cross-cutting concerns (Theme, Language, Currency updates)
- **Dependency Injection** - Hilt for compile-time DI

---

## Module Structure

### Module Dependency Graph

```
                    ┌──────┐
                    │ app  │
                    └───┬──┘
                        │
        ┌───────────────┼───────────────┐
        │               │               │
   ┌────▼────┐    ┌────▼────┐    ┌────▼────┐
   │ feature │    │ core:ui │    │  data   │
   │ modules │    └─────────┘    └────┬────┘
   └─────────┘                        │
                                 ┌────▼────┐
                                 │ domain  │
                                 └─────────┘
```

### 1. App Module (`/app`)

**Type**: Android Application Module
**Purpose**: Application entry point and navigation orchestration

**Key Components**:
- `App.kt` - Application class with Hilt, WorkManager, currency update scheduling
- `MainActivity.kt` - Single activity with Compose UI, theme/language management
- `AppNavGraph.kt` - Navigation graph builder using NavigationFactory pattern
- `default_exchange_rates.json` - Fallback currency data (160+ currencies)

**Dependencies**: All feature modules, core:ui, data, domain

---

### 2. Domain Module (`/domain`)

**Type**: Pure Kotlin/Java Library Module
**Purpose**: Business logic layer - entities, use cases, repository interfaces

**Package Structure**:
```
domain/
├── model/              # Business entities
│   ├── CurrencyEntity.kt    # @Entity for Room, @Parcelize
│   ├── Language.kt          # Enum: EN, DE, FR, ES, PT, IT
│   └── Theme.kt             # Enum: LIGHT, DARK, SYSTEM
│
├── repository/         # Repository interfaces
│   └── ExchangeRateRepository.kt
│
├── usecase/           # Use cases
│   └── GetExchangeRatesUseCase.kt
│
├── manager/           # Manager interfaces
│   ├── preferences/   # SharedPreferencesManager
│   ├── file/         # FileManager
│   ├── language/     # LanguageManager
│   ├── theme/        # ThemeManager
│   └── currency/     # CurrencyUpdateManager
│
├── di/               # DomainModule (Hilt)
├── util/             # CurrencyUtils (flag emoji, formatting)
└── ext/              # Extension functions
```

**Key Features**:
- Clean interfaces for data access
- Room entity definitions
- Business logic utilities (currency formatting, flag emoji generation)

---

### 3. Data Module (`/data`)

**Type**: Android Library Module
**Purpose**: Data layer - repository implementations, local/remote data sources

**Package Structure**:
```
data/
├── repository/        # Repository implementations
│   └── ExchangeRateRepositoryImpl.kt
│
├── room/             # Room Database
│   ├── AppDatabase.kt          # Room DB with CurrencyDao
│   └── dao/
│       └── CurrencyDao.kt      # @Dao interface
│
├── remote/           # Network layer
│   ├── api/
│   │   └── ExchangeRateApi.kt  # Retrofit API
│   ├── model/
│   │   └── ExchangeRateResponse.kt
│   └── mapper/
│       └── ExchangeRateMapper.kt
│
├── manager/          # Manager implementations
│   ├── preferences/  # SharedPreferencesManagerImpl
│   ├── file/        # FileManagerImpl
│   ├── language/    # LanguageManagerImpl
│   ├── theme/       # ThemeManagerImpl
│   └── currency/    # CurrencyUpdateManagerImpl
│
├── worker/          # WorkManager
│   └── CurrencyUpdateWorker.kt  # Weekly background updates
│
├── di/              # DataModule, NetworkModule (Hilt)
├── initializer/     # App initializers
├── util/            # DefaultCurrencyProvider
└── exception/       # Exception handlers
```

**Key Technologies**:
- **Room Database** - Local currency storage with DAO pattern
- **Retrofit + OkHttp** - REST API client with logging
- **Kotlinx Serialization** - JSON parsing
- **WorkManager** - Weekly background currency data sync
- **DataStore/SharedPreferences** - User preferences storage

**API Integration**: Exchange Rate API for live currency conversion rates

---

### 4. Core:UI Module (`/core/ui`)

**Type**: Android Library Module
**Purpose**: Shared UI components, theme system, navigation infrastructure

**Package Structure**:
```
core/ui/
├── components/          # Reusable Compose components
│   ├── WheelPicker.kt           # Currency wheel picker
│   ├── CurrencySelectorSheet.kt # Bottom sheet with search
│   ├── DropdownTextField.kt
│   ├── LoadingDialog.kt
│   └── AppTextField.kt
│
├── theme/              # Material 3 Design System
│   ├── Color.kt        # Color palette
│   ├── Theme.kt        # Theme composable
│   └── Type.kt         # Typography
│
├── navigation/         # Navigation infrastructure
│   ├── NavigationFactory.kt      # Interface for feature APIs
│   ├── NavControllerExtension.kt
│   └── NavGraphBuilderExtensions.kt
│
└── util/              # UI utilities and extensions
```

**Key Features**:
- Material 3 design components
- Custom navigation factory pattern for modular routing
- Currency selector with wheel picker and search
- Coil image loading integration
- Accompanist permissions and system UI controller

---

### 5. Feature Modules (`/feature`)

All feature modules follow a consistent structure:
- `ui/` - Compose screens, ViewModels
- `api/` - Feature API implementing `NavigationFactory`
- `di/` - Hilt dependency modules
- Custom `androidUi.gradle.kts` plugin for standardization

#### Feature:Onboarding (`/feature/onboarding`)

**Purpose**: First-launch onboarding experience

**Structure**:
```
onboarding/
├── ui/
│   ├── OnboardingScreen.kt     # Main screen with ViewPager
│   └── pages/                  # Onboarding page composables
│
├── di/                         # DependencyProvider
└── api/                        # OnboardingFeatureApi
```

**Features**:
- Multi-page onboarding with swipe navigation
- App feature introduction
- Permission request flow

---

#### Feature:Camera (`/feature/camera`)

**Purpose**: Main feature - camera-based price scanning and conversion

**Structure**:
```
camera/
├── ui/
│   ├── CameraScreen.kt              # Screen composable
│   ├── CameraViewModel.kt           # State management
│   ├── CameraContent.kt             # Main content layout
│   │
│   ├── camera/                      # Camera logic
│   │   ├── TextRecognitionAnalyzer.kt  # ML Kit OCR
│   │   └── DebugInfo.kt                # OCR debug overlay
│   │
│   ├── elements/                    # UI components
│   │   ├── CameraPreviewContent.kt
│   │   ├── ConversionResultCard.kt
│   │   ├── CurrencySelectorButton.kt
│   │   ├── FlashlightButton.kt
│   │   └── PermissionHandlingContent.kt
│   │
│   └── alert/                       # Permission alerts
│
├── di/                              # CameraModule (Hilt)
└── api/                             # CameraFeatureApi
```

**Key Technologies**:
- **CameraX** - Modern camera API with lifecycle awareness
- **ML Kit Text Recognition** - OCR for price detection
- **Regex Pattern Matching** - Price extraction from text
- Real-time currency conversion
- Flashlight toggle support
- Debug overlay showing detected text blocks

**Flow**:
1. Camera captures frames
2. ML Kit analyzes text in real-time
3. Regex extracts price amounts
4. Converts to target currency using local exchange rates
5. Displays result in overlay card

---

#### Feature:Settings (`/feature/settings`)

**Purpose**: App configuration - currencies, language, theme, about

**Structure**:
```
settings/
├── ui/
│   ├── SettingsScreen.kt           # Main settings screen
│   ├── SettingsViewModel.kt        # State management
│   │
│   ├── currencies/                 # Currency management
│   │   ├── CurrenciesScreen.kt
│   │   └── CurrenciesViewModel.kt
│   │
│   └── components/                 # Settings components
│       ├── LanguageSelectionSheet.kt
│       ├── ThemeSelectionSheet.kt
│       ├── CurrencyItem.kt
│       └── SettingsItem.kt
│
├── navigation/                     # SettingsNavigation
├── di/                            # SettingsModule
├── arch/                          # Architecture components
└── api/                           # SettingsFeatureApi
```

**Features**:
- **Currency Management**: Enable/disable currencies, set defaults
- **Language Selection**: 6 languages (EN, DE, FR, ES, PT, IT)
- **Theme Selection**: Light, Dark, System default
- **About Section**: Version info, privacy policy, terms of service
- External link handling with browser intents

---

## Key Features

### 1. Price Scanning & Conversion

- Real-time camera preview with CameraX
- ML Kit OCR text recognition
- Automatic price amount extraction
- Instant currency conversion
- Visual overlay showing detected text
- Offline conversion after initial sync

### 2. Currency Management

- Support for 160+ world currencies
- Local Room database storage
- Weekly automatic updates via WorkManager
- Fallback to bundled JSON data
- Enable/disable currencies for faster selection
- Country flag display using emoji
- Searchable currency selector with wheel picker

### 3. Localization

- 6 supported languages (English, German, French, Spanish, Portuguese, Italian)
- Runtime language switching
- Localized string resources (`values-*/strings.xml`)
- Persistent language preference

### 4. Theme System

- Material 3 design
- Light/Dark/System theme modes
- Dynamic color support (Material You)
- Persistent theme preference

### 5. Background Sync

- WorkManager for periodic updates
- Weekly exchange rate refresh
- Battery-optimized scheduling
- Offline-first architecture

---

## Technology Stack

### UI Layer

| Technology | Version | Purpose |
|-----------|---------|---------|
| Jetpack Compose | BOM 2025.08.00 | Declarative UI framework |
| Material 3 | 1.3.2 | Material Design components |
| Compose Navigation | 2.9.3 | Screen navigation |
| Coil | 2.7.0 | Image loading |
| Accompanist | 0.37.3 | Permissions, system UI |

### Architecture

| Technology | Version | Purpose |
|-----------|---------|---------|
| Hilt | 2.57.1 | Dependency injection |
| Kotlin Coroutines | 1.10.2 | Asynchronous programming |
| Kotlin Flow | - | Reactive streams |
| ViewModel | - | State management |

### Data Layer

| Technology | Version | Purpose |
|-----------|---------|---------|
| Room | 2.7.2 | Local database |
| DataStore | 1.1.7 | Preferences storage |
| Retrofit | 3.0.0 | HTTP client |
| OkHttp | 5.1.0 | Network layer |
| Kotlinx Serialization | 1.9.0 | JSON parsing |

### Camera & ML

| Technology | Version | Purpose |
|-----------|---------|---------|
| CameraX | 1.5.0-rc01 | Camera implementation |
| ML Kit Text Recognition | 16.0.1 | OCR for price scanning |

### Background Processing

| Technology | Version | Purpose |
|-----------|---------|---------|
| WorkManager | 2.10.3 | Background tasks |

### Monitoring

| Technology | Version | Purpose |
|-----------|---------|---------|
| Firebase Crashlytics | BOM 34.1.0 | Crash reporting |

### Build Tools

| Technology | Version | Purpose |
|-----------|---------|---------|
| Kotlin | 2.2.0 | Programming language |
| Gradle | 8.11.1 | Build system |
| KSP | 2.2.0-2.0.2 | Symbol processing |
| AGP | 8.8.2 | Android Gradle Plugin |

---

## Project Structure

```
Pricelator/
│
├── app/                              # Main application module
│   ├── src/main/
│   │   ├── java/com/kostryk/pricelator/
│   │   │   ├── App.kt                # Application class
│   │   │   ├── MainActivity.kt       # Single activity
│   │   │   └── AppNavGraph.kt        # Navigation setup
│   │   │
│   │   ├── assets/
│   │   │   └── default_exchange_rates.json  # Fallback data
│   │   │
│   │   ├── res/                      # Resources
│   │   └── AndroidManifest.xml
│   │
│   └── build.gradle.kts
│
├── domain/                           # Business logic layer
│   └── src/main/java/com/kostryk/pricelator/domain/
│       ├── model/                    # Entities
│       ├── repository/               # Interfaces
│       ├── usecase/                  # Use cases
│       ├── manager/                  # Managers
│       ├── util/                     # Utilities
│       └── di/                       # DI modules
│
├── data/                             # Data layer
│   └── src/main/java/com/kostryk/pricelator/data/
│       ├── repository/               # Implementations
│       ├── room/                     # Room DB
│       ├── remote/                   # Retrofit API
│       ├── manager/                  # Manager impls
│       ├── worker/                   # WorkManager
│       ├── di/                       # DI modules
│       └── util/
│
├── core/                             # Core utilities
│   └── ui/                           # Shared UI module
│       └── src/main/java/com/kostryk/pricelator/
│           ├── ui/
│           │   ├── components/       # Reusable components
│           │   ├── theme/            # Material 3 theme
│           │   └── navigation/       # Navigation infra
│           │
│           └── util/                 # UI utilities
│
├── feature/                          # Feature modules
│   ├── onboarding/                   # Onboarding flow
│   │   ├── ui/
│   │   ├── api/
│   │   └── di/
│   │
│   ├── camera/                       # Camera + OCR
│   │   ├── ui/
│   │   ├── api/
│   │   └── di/
│   │
│   └── settings/                     # Settings screen
│       ├── ui/
│       ├── api/
│       ├── di/
│       └── navigation/
│
├── buildSrc/                         # Build configuration
│   └── src/main/
│       ├── kotlin/
│       │   ├── androidUi.gradle.kts  # Feature plugin
│       │   └── androidApi.gradle.kts # API plugin
│       │
│       └── java/
│           ├── Version.kt            # Version constants
│           ├── App.kt                # App config
│           └── Build.kt              # Build types
│
├── gradle/
│   └── libs.versions.toml            # Version catalog
│
├── build.gradle.kts                  # Root build script
├── settings.gradle.kts               # Module configuration
├── gradle.properties                 # Gradle properties
├── .gitignore
└── README.md
```

---

## Navigation Architecture

### Feature API Pattern

The project uses a modular navigation system based on the `NavigationFactory` interface:

```kotlin
// core/ui/navigation/NavigationFactory.kt
interface NavigationFactory {
    fun create(navGraphBuilder: NavGraphBuilder, navController: NavController)
}
```

Each feature module exposes its navigation through a `*FeatureApi` class:

```kotlin
// feature/camera/api/CameraFeatureApi.kt
class CameraFeatureApi : NavigationFactory {
    override fun create(navGraphBuilder: NavGraphBuilder, navController: NavController) {
        navGraphBuilder.composable("camera") {
            CameraScreen(navController)
        }
    }
}
```

The app module collects all factories via Hilt:

```kotlin
// app/AppNavGraph.kt
@Composable
fun AppNavGraph(
    navController: NavHostController,
    navigationFactories: Set<NavigationFactory>
) {
    NavHost(navController, startDestination = "onboarding") {
        navigationFactories.forEach { factory ->
            factory.create(this, navController)
        }
    }
}
```

**Benefits**:
- Decoupled feature modules
- Scalable navigation system
- Type-safe routing
- Easy to add new features

---

## Data Flow

### Currency Conversion Flow Example

```
┌──────────────────────────┐
│   CameraScreen (UI)      │
│   User scans price tag   │
└───────────┬──────────────┘
            │
            ▼
┌──────────────────────────┐
│   CameraViewModel        │
│   StateFlow management   │
└───────────┬──────────────┘
            │
            ▼
┌──────────────────────────┐
│ GetExchangeRatesUseCase  │
│   Business logic         │
└───────────┬──────────────┘
            │
            ▼
┌──────────────────────────┐
│ ExchangeRateRepository   │
│   (Interface)            │
└───────────┬──────────────┘
            │
            ▼
┌──────────────────────────┐
│ExchangeRateRepositoryImpl│
└───────┬─────────┬────────┘
        │         │
        ▼         ▼
┌─────────┐  ┌────────┐
│   API   │  │  Room  │
│ Retrofit│  │  DAO   │
└─────────┘  └────────┘
```

### Data Sync Flow

1. **Initial Load**: App checks Room database for currency data
2. **API Fetch**: If empty, fetches from Exchange Rate API
3. **Fallback**: If API fails, loads from `default_exchange_rates.json`
4. **Weekly Sync**: WorkManager schedules background updates
5. **Offline Mode**: Uses cached Room data for conversions

---

## Build Configuration

### Gradle Modules

The project uses custom Gradle plugins for consistent configuration:

**buildSrc Structure**:
```
buildSrc/
├── androidApi.gradle.kts    # API module configuration
├── androidUi.gradle.kts     # Feature module configuration
└── src/main/java/
    ├── App.kt              # applicationId, versionCode, versionName
    ├── Version.kt          # Kotlin, Java, Gradle versions
    └── Build.kt            # Build types and signing
```

### Version Catalog (`gradle/libs.versions.toml`)

Centralized dependency management using Gradle Version Catalog for consistency across modules.

### Build Variants

- **Debug**: Debuggable, logging enabled, Crashlytics disabled
- **Release**: Minified, obfuscated (R8), Crashlytics enabled

### ProGuard/R8 Configuration

- Code shrinking enabled
- Obfuscation enabled
- Resource shrinking enabled
- Rules for Room, Retrofit, Kotlinx Serialization

---

## Getting Started

### Prerequisites

- **Android Studio**: Ladybug | 2024.2.2 or later
- **JDK**: 17
- **Gradle**: 8.11.1 (wrapper included)
- **Min Android Version**: Android 10 (API 29)

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Pricelator
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the Pricelator directory

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download

4. **Add Firebase Configuration (if needed)**
   - Place `google-services.json` in `app/` directory
   - Required for Crashlytics in production builds

5. **Build and Run**
   - Select a device/emulator
   - Click "Run" or press Shift + F10

### Project Configuration

**Key Files**:
- `build.gradle.kts` - Root build configuration
- `settings.gradle.kts` - Module inclusion and repositories
- `gradle.properties` - Gradle properties and flags
- `buildSrc/src/main/java/App.kt` - App version and package info

### API Configuration

The app uses the Exchange Rate API. The base URL is configured in:
```kotlin
// data/di/NetworkModule.kt
@Provides
fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.exchangerate-api.com/v4/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
}
```

---

## Development Guidelines

### Adding a New Feature Module

1. Create new module directory: `feature/new-feature/`
2. Apply plugin: `plugins { id("androidUi") }`
3. Create `api/` package with `NewFeatureApi : NavigationFactory`
4. Implement UI in `ui/` package
5. Add Hilt module in `di/`
6. Register in app module's navigation

### Code Style

- **Kotlin**: Follow official Kotlin coding conventions
- **Compose**: Use Material 3 components from `core:ui`
- **State Management**: Use StateFlow in ViewModels
- **Naming**: Clear, descriptive names (no abbreviations)
- **Comments**: Document complex logic, avoid obvious comments

### Testing Strategy

- **Unit Tests**: Domain layer use cases and utilities
- **Integration Tests**: Repository implementations with Room
- **UI Tests**: Compose screen tests with test tags

---

## Contributing

### Git Workflow

- **Main Branch**: `main` - Production-ready code
- **Development Branch**: `develop` - Active development
- **Feature Branches**: `feature/CNK-XX_description`
- **Bug Fixes**: `bugfix/CNK-XX_description`

### Commit Messages

Follow conventional commits:
```
<type>(<scope>): <description>

[optional body]
```

Types: feat, fix, docs, style, refactor, test, chore

### Pull Request Process

1. Create feature branch from `develop`
2. Implement changes with tests
3. Update documentation if needed
4. Create PR to `develop` with description
5. Wait for review and CI checks
6. Merge after approval

---

## License

[Add your license information here]

---

## Contact

[Add contact information or team details here]

---

## Additional Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Clean Architecture Guide](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [CameraX Documentation](https://developer.android.com/training/camerax)
- [ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition/v2/android)

---

**Last Updated**: December 23, 2025
**Document Version**: 1.0
