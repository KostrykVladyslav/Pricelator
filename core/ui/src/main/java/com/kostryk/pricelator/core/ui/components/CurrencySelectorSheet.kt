package com.kostryk.pricelator.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kostryk.pricelator.core.ui.R
import com.kostryk.pricelator.domain.model.CurrencyEntity
import com.kostryk.pricelator.ui.components.CurrencyWheelPicker
import com.kostryk.pricelator.ui.theme.AppFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectorSheet(
    currencies: List<CurrencyEntity>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val selectedIndex = currencies.indexOfFirst { it.symbol == selectedCurrency }
    var textFilterValue by remember { mutableStateOf("") }
    var snappedItem by remember { mutableStateOf<CurrencyEntity?>(null) }
    val filteredCurrencies = remember(textFilterValue) {
        if (textFilterValue.isEmpty()) currencies else {
            currencies.filter {
                it.symbol.contains(
                    other = textFilterValue,
                    ignoreCase = true
                ) || it.name.contains(
                    other = textFilterValue,
                    ignoreCase = true
                )
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 104.dp, height = 6.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }

                Text(
                    text = stringResource(R.string.save),
                    fontFamily = AppFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            snappedItem?.symbol?.let { onCurrencySelected(it) }
                            onDismiss()
                        }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }

            TextField(
                value = textFilterValue,
                onValueChange = { editFieldValue ->
                    textFilterValue = editFieldValue
                },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.filter)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp, end = 36.dp, top = 12.dp),
                trailingIcon = {
                    if (textFilterValue.isNotEmpty()) {
                        IconButton(onClick = { textFilterValue = "" }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                CurrencyWheelPicker(
                    items = filteredCurrencies,
                    selectedIndex = selectedIndex,
                    onItemSnapped = { item -> snappedItem = item }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
@Preview
private fun CurrencySelectorSheetPreview() {
    Box {
        CurrencySelectorSheet(
            currencies = listOf(),
            selectedCurrency = "UAH",
            onCurrencySelected = {},
            onDismiss = { }
        )
    }
}
