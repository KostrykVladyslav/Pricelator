package com.kostryk.pricelator.settings.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.kostryk.pricelator.ui.navigation.NavigationFactory
import com.kostryk.pricelator.settings.navigation.SettingsRoutes
import com.kostryk.pricelator.settings.navigation.settingsNavGraph
import javax.inject.Inject

class SettingsFeatureApi @Inject constructor() : NavigationFactory {

    override fun screen(builder: NavGraphBuilder, navController: NavHostController) {
        builder.settingsNavGraph(navController)
    }

    companion object {
        const val settingsRoute = SettingsRoutes.SETTINGS
    }
}
