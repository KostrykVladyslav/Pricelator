package com.kostryk.pricelator.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.kostryk.pricelator.settings.ui.*
import androidx.compose.ui.res.stringResource
import com.kostryk.pricelator.core.ui.R
import com.kostryk.pricelator.settings.ui.currencies.CurrenciesScreen
import com.kostryk.pricelator.settings.ui.legal.LegalDocuments
import com.kostryk.pricelator.settings.ui.legal.LegalScreen

object SettingsRoutes {
    const val SETTINGS = "settings"
    const val CURRENCIES = "currencies"
    const val PRIVACY_POLICY = "privacy_policy"
    const val TERMS = "terms_and_conditions"
}

fun NavGraphBuilder.settingsNavGraph(navController: NavHostController) {
    composable(SettingsRoutes.SETTINGS) {
        SettingsScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToCurrencies = { navController.navigate(SettingsRoutes.CURRENCIES) },
            onNavigateToPrivacyPolicy = { navController.navigate(SettingsRoutes.PRIVACY_POLICY) },
            onNavigateToTerms = { navController.navigate(SettingsRoutes.TERMS) }
        )
    }

    composable(SettingsRoutes.CURRENCIES) {
        CurrenciesScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(SettingsRoutes.PRIVACY_POLICY) {
        LegalScreen(
            title = stringResource(R.string.privacy_policy),
            document = LegalDocuments.privacyPolicy,
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(SettingsRoutes.TERMS) {
        LegalScreen(
            title = stringResource(R.string.terms_conditions),
            document = LegalDocuments.termsAndConditions,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
