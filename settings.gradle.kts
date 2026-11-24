plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "Pricelator"
include(":app")
include(":core")
include(":core:ui")
include(":data")
include(":domain")
include(":feature")
include(":feature:onboarding")
include(":feature:camera")
include(":feature:settings")