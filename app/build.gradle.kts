plugins {
    id(libs.plugins.androidApplication.get().pluginId)
    id(libs.plugins.jetbrainsKotlinAndroid.get().pluginId)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.dagger.hilt.android")
    id(libs.plugins.ksp.get().pluginId)
    id(libs.plugins.compose.compiler.get().pluginId)
}

android {
    namespace = App.namespace
    compileSdk = App.compileSdk

    defaultConfig {
        applicationId = App.namespace
        minSdk = App.minSdk
        targetSdk = App.targetSdk
        versionCode = App.versionCode
        versionName = App.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create(Build.release) {
            App.properties.load(project.rootProject.file("keystore.properties").inputStream())
            storeFile = File(rootDir, App.properties.storeFilePath())
            storePassword = App.properties.storePassword()
            keyPassword = App.properties.keyPassword()
            keyAlias = App.properties.keyAlias()
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName(Build.release)
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            manifestPlaceholders["enable_firebase_crashlytics"] = true
        }

        debug {
            manifestPlaceholders["enable_firebase_crashlytics"] = false
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a")
            isUniversalApk = false
        }
    }

    compileOptions {
        sourceCompatibility = Version.sourceCompatibility
        targetCompatibility = Version.targetCompatibility
    }
    buildFeatures {
        compose = true
    }

    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.camera)
    implementation(projects.feature.settings)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.composeUIGraphics)
    implementation(libs.composeUIToolingPreview)
    implementation(libs.composeMaterial3)
    implementation(libs.composeNavigation)
    implementation(libs.lifecycleCompose)
    implementation(libs.hiltComposeNavigation)
    implementation(libs.coil)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.kotlin.bom))
    implementation(libs.appcompat)
    implementation(libs.composeTracing)

    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    implementation(libs.work.ktx)
    implementation(libs.hilt.work)
}