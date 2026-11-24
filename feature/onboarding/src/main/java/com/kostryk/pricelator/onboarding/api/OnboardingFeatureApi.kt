package com.kostryk.pricelator.onboarding.api

import android.content.Context
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import com.kostryk.pricelator.ui.navigation.NavigationFactory
import com.kostryk.pricelator.ui.util.inject
import com.kostryk.pricelator.onboarding.di.DependencyProvider
import com.kostryk.pricelator.onboarding.ui.OnboardingScreen

class OnboardingFeatureApi(context: Context) : NavigationFactory {

    private val di = context.inject(DependencyProvider::class.java)

    val route: String = "onboarding"

    override fun screen(
        builder: NavGraphBuilder,
        navController: NavHostController
    ) {
        builder.composable(route = route) {
            OnboardingScreen {
                di.sharedPreferencesManager.put(SharedPreferencesManager.KEY_FIRST_LAUNCH, false)
                navController.navigate(
                    di.cameraFeatureApi.route, navOptions = NavOptions.Builder()
                        .setPopUpTo(navController.currentDestination?.id ?: 0, true)
                        .build()
                )
            }
        }
    }
}