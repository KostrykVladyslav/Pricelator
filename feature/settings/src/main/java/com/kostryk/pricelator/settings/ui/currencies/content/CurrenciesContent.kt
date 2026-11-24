package com.kostryk.pricelator.settings.ui.currencies.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kostryk.pricelator.core.ui.R
import com.kostryk.pricelator.core.ui.components.CurrencySelectorSheet
import com.kostryk.pricelator.domain.util.CurrencyUtils
import com.kostryk.pricelator.settings.ui.currencies.CurrenciesState
import com.kostryk.pricelator.settings.ui.currencies.content.item.CurrencyItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrenciesContent(
    state: CurrenciesState,
    onNavigateBack: () -> Unit,
    onUpdateCurrenciesClicked: () -> Unit,
    onDefaultFromCurrencySelected: (String) -> Unit,
    onDefaultToCurrencySelected: (String) -> Unit,
    onClearError: () -> Unit
) {
    var showFromCurrencySelector by remember { mutableStateOf(false) }
    var showToCurrencySelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.currencies),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(19.dp))
                    .background(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(19.dp)
                    )
            ) {
                CurrencyItem(
                    label = stringResource(R.string.default_from),
                    countryFlag = CurrencyUtils.getCountryFlag(state.defaultFromCurrency),
                    countryName = CurrencyUtils.formatCurrencyName(state.defaultFromCurrency),
                    onClick = { showFromCurrencySelector = true }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                CurrencyItem(
                    label = stringResource(R.string.default_to),
                    countryFlag = CurrencyUtils.getCountryFlag(state.defaultToCurrency),
                    countryName = CurrencyUtils.formatCurrencyName(state.defaultToCurrency),
                    onClick = { showToCurrencySelector = true }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onUpdateCurrenciesClicked)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.update_currencies),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.last_update, state.lastUpdateTime),
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                    if (state.isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    if (showFromCurrencySelector) {
        CurrencySelectorSheet(
            currencies = state.currencies,
            selectedCurrency = state.defaultFromCurrency,
            onCurrencySelected = onDefaultFromCurrencySelected,
            onDismiss = { showFromCurrencySelector = false }
        )
    }

    if (showToCurrencySelector) {
        CurrencySelectorSheet(
            currencies = state.currencies,
            selectedCurrency = state.defaultToCurrency,
            onCurrencySelected = onDefaultToCurrencySelected,
            onDismiss = { showToCurrencySelector = false }
        )
    }

    state.errorMessage?.let { error ->
        LaunchedEffect(error) {
            onClearError()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrenciesScreenPreview() {
    CurrenciesContent(
        state = CurrenciesState(
            defaultFromCurrency = "UAH",
            defaultToCurrency = "USD",
            lastUpdateTime = "12.12.2023 12:00",
            isUpdating = false,
            errorMessage = null,
            currencies = emptyList()
        ),
        onNavigateBack = {},
        onUpdateCurrenciesClicked = {},
        onDefaultFromCurrencySelected = {},
        onDefaultToCurrencySelected = {},
        onClearError = {}
    )
}