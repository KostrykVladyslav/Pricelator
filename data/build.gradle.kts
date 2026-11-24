import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlinx-serialization")
    id("com.google.dagger.hilt.android")
    id(libs.plugins.ksp.get().pluginId)
}

// Secrets live in local.properties (git-ignored) and reach the code only through BuildConfig.
val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

fun localProperty(name: String): String =
    localProperties.getProperty(name) ?: System.getenv(name) ?: run {
        logger.warn("$name is not set in local.properties or the environment")
        ""
    }

android {
    namespace = "${App.namespace}.data"
    compileSdk = App.compileSdk

    defaultConfig {
        minSdk = App.minSdk
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "EXCHANGE_RATE_BASE_URL", "\"${localProperty("EXCHANGE_RATE_BASE_URL")}\"")
        buildConfigField("String", "EXCHANGE_RATE_API_KEY", "\"${localProperty("EXCHANGE_RATE_API_KEY")}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = Version.sourceCompatibility
        targetCompatibility = Version.targetCompatibility
    }
    kotlin {
        jvmToolchain(Version.jvmVersion)
    }
}

dependencies {
    implementation(projects.domain)

    implementation(libs.kotlin.std.lib)
    implementation(libs.coroutines.core)

    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.serialization.json)
    implementation(libs.serialization.converter)

    implementation(libs.hilt)
    implementation(libs.appcompat)
    ksp(libs.hilt.compiler)

    implementation(libs.work.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}