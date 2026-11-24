package com.kostryk.pricelator.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kostryk.pricelator.settings.ui.components.LanguageSelectionSheet
import com.kostryk.pricelator.settings.ui.components.ThemeSelectionSheet
import com.kostryk.pricelator.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCurrencies: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
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
                SettingsItem(
                    title = stringResource(
                        R.string.language_format,
                        state.selectedLanguage.displayName
                    ),
                    onClick = { showLanguageSheet = true }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                SettingsItem(
                    title = stringResource(
                        R.string.theme_format,
                        state.selectedTheme.name.lowercase().replace("_", " ")
                            .replaceFirstChar { it.uppercase() }),
                    onClick = { showThemeSheet = true }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                SettingsItem(
                    title = stringResource(R.string.currencies),
                    onClick = onNavigateToCurrencies
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                SettingsItem(
                    title = stringResource(R.string.privacy_policy),
                    onClick = onNavigateToPrivacyPolicy
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                SettingsItem(
                    title = stringResource(R.string.terms_conditions),
                    onClick = onNavigateToTerms
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.version_format, state.appVersion))
                        appendLine()
                        append(stringResource(R.string.by_company))
                        append(" ")
                        withLink(
                            LinkAnnotation.Url(
                                url = "https://github.com/KostrykVladyslav",
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.onBackground,
                                        textDecoration = TextDecoration.Underline
                                    )
                                )
                            )
                        ) { append(state.companyName) }
                    },
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }

    if (showLanguageSheet) {
        LanguageSelectionSheet(
            selectedLanguage = state.selectedLanguage,
            onLanguageSelected = { language ->
                viewModel.onLanguageSelected(language)
                showLanguageSheet = false
            },
            onDismiss = { showLanguageSheet = false }
        )
    }

    if (showThemeSheet) {
        ThemeSelectionSheet(
            selectedTheme = state.selectedTheme,
            onThemeSelected = { theme ->
                viewModel.onThemeSelected(theme)
                showThemeSheet = false
            },
            onDismiss = { showThemeSheet = false }
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
@Preview
private fun SettingsPreview() {
    SettingsScreen(
        onNavigateBack = {},
        onNavigateToCurrencies = {},
        onNavigateToPrivacyPolicy = {},
        onNavigateToTerms = {}
    )
}
