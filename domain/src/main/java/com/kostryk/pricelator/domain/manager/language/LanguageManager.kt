package com.kostryk.pricelator.domain.manager.language

import com.kostryk.pricelator.domain.model.Language
import kotlinx.coroutines.flow.StateFlow

interface LanguageManager {
    val currentLanguage: StateFlow<Language>
    fun setLanguage(language: Language)
    fun getLanguage(): Language
}
