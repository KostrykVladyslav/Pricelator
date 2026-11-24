package com.kostryk.pricelator.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun interface NavigationFactory {
    fun screen(builder: NavGraphBuilder, navController: NavHostController)
}