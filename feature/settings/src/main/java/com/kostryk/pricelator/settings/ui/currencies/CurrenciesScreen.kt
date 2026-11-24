package com.kostryk.pricelator.settings.ui.currencies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.kostryk.pricelator.settings.ui.currencies.content.CurrenciesContent

@Composable
fun CurrenciesScreen(
    onNavigateBack: () -> Unit,
    viewModel: CurrenciesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    CurrenciesContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onUpdateCurrenciesClicked = viewModel::onUpdateCurrenciesClicked,
        onDefaultFromCurrencySelected = viewModel::onDefaultFromCurrencySelected,
        onDefaultToCurrencySelected = viewModel::onDefaultToCurrencySelected,
        onClearError = viewModel::clearError
    )
}