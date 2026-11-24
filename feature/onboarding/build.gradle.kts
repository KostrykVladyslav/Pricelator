plugins {
    id("androidUi")
}

android {
    namespace = "${App.namespace}.onboarding"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.domain)
    implementation(projects.feature.camera)
}