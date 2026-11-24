package com.kostryk.pricelator.camera.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.kostryk.pricelator.camera.ui.CameraScreen
import com.kostryk.pricelator.ui.navigation.NavigationFactory
import com.kostryk.pricelator.settings.api.SettingsFeatureApi

class CameraFeatureApi : NavigationFactory {

    val route: String = "camera"

    override fun screen(
        builder: NavGraphBuilder,
        navController: NavHostController
    ) {
        builder.composable(route) {
            CameraScreen { navController.navigate(SettingsFeatureApi.settingsRoute) }
        }
    }
}