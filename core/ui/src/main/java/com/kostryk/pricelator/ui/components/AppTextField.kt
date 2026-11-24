package com.kostryk.pricelator.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    label: Int,
    isEnabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChanged(it) },
        label = { Text(stringResource(label)) },
        singleLine = true,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Black,
        )
    )
}