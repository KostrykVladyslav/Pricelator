package com.kostryk.pricelator.data.manager.language

import com.kostryk.pricelator.domain.manager.language.LanguageManager
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import com.kostryk.pricelator.domain.model.Language
import com.kostryk.pricelator.domain.util.CurrencyUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManagerImpl @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) : LanguageManager {

    private val _currentLanguage = MutableStateFlow(loadLanguageFromPreferences())
    override val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    init {
        CurrencyUtils.setLocale(Locale(getLanguage().code))
    }

    private fun loadLanguageFromPreferences(): Language {
        val languageCode = sharedPreferencesManager.get(
            SharedPreferencesManager.KEY_APP_LANGUAGE,
            "en"
        )
        return Language.fromCode(languageCode)
    }

    override fun setLanguage(language: Language) {
        sharedPreferencesManager.put(
            SharedPreferencesManager.KEY_APP_LANGUAGE,
            language.code
        )
        _currentLanguage.value = language
        CurrencyUtils.setLocale(Locale(language.code))
    }

    override fun getLanguage(): Language {
        return _currentLanguage.value
    }
}
