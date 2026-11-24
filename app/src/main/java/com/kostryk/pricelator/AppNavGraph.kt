package com.kostryk.pricelator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kostryk.pricelator.ui.navigation.NavigationFactory

@Composable
fun AppNavGraph(
    modifier: Modifier,
    navController: NavHostController,
    navigationFactories: Set<NavigationFactory>,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination
    ) {
        navigationFactories.forEach {
            it.screen(this, navController)
        }
    }
}