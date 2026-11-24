package com.kostryk.pricelator.data.manager.theme

import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import com.kostryk.pricelator.domain.manager.theme.ThemeManager
import com.kostryk.pricelator.domain.model.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManagerImpl @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) : ThemeManager {

    private val _currentTheme = MutableStateFlow(loadThemeFromPreferences())
    override val currentTheme: StateFlow<Theme> = _currentTheme.asStateFlow()

    private fun loadThemeFromPreferences(): Theme {
        val themeValue = sharedPreferencesManager.get(
            SharedPreferencesManager.KEY_APP_THEME,
            "system"
        )
        return Theme.fromString(themeValue)
    }

    override fun setTheme(theme: Theme) {
        sharedPreferencesManager.put(
            SharedPreferencesManager.KEY_APP_THEME,
            theme.toStorageString()
        )
        _currentTheme.value = theme
    }

    override fun getTheme(): Theme {
        return _currentTheme.value
    }
}
