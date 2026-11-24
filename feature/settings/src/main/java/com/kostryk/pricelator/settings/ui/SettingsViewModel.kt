package com.kostryk.pricelator.settings.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostryk.pricelator.domain.manager.currency.CurrencyUpdateManager
import com.kostryk.pricelator.domain.manager.language.LanguageManager
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import com.kostryk.pricelator.domain.manager.theme.ThemeManager
import com.kostryk.pricelator.domain.model.Language
import com.kostryk.pricelator.domain.model.Theme
import com.kostryk.pricelator.settings.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val currencyUpdateManager: CurrencyUpdateManager,
    private val themeManager: ThemeManager,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _state.value = _state.value.copy(
            selectedTheme = themeManager.getTheme(),
            selectedLanguage = languageManager.getLanguage(),
            appVersion = BuildConfig.VERSION_NAME,
            companyName = "Vladyslav Kostryk"
        )
    }

    fun onThemeSelected(theme: Theme) {
        viewModelScope.launch {
            themeManager.setTheme(theme)
            _state.value = _state.value.copy(selectedTheme = theme)
        }
    }

    fun onLanguageSelected(language: Language) {
        viewModelScope.launch {
            languageManager.setLanguage(language)
            delay(100)
            _state.value = _state.value.copy(selectedLanguage = language)
        }
    }
}

@Immutable
data class SettingsState(
    val selectedTheme: Theme = Theme.SYSTEM_DEFAULT,
    val selectedLanguage: Language = Language.ENGLISH,
    val appVersion: String = "",
    val companyName: String = ""
)
