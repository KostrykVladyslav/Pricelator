plugins {
    id("androidUi")
}

android {
    namespace = "${App.namespace}.camera"
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.domain)
    implementation(projects.feature.settings)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.accompanistPermissions)

    implementation(libs.onnxruntime.android)
}