package com.kostryk.pricelator.domain.manager.theme

import com.kostryk.pricelator.domain.model.Theme
import kotlinx.coroutines.flow.StateFlow

interface ThemeManager {
    val currentTheme: StateFlow<Theme>
    fun setTheme(theme: Theme)
    fun getTheme(): Theme
}
