plugins {
    id("androidUi")
}

android {
    namespace = "${App.namespace}.settings"

    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${App.versionName}\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.domain)
}