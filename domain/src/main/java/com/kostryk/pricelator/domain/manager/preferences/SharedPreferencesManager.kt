package com.kostryk.pricelator.domain.manager.preferences

interface SharedPreferencesManager {

    fun <T> put(key: String, value: T)

    fun <T> get(key: String, defValue: T): T

    companion object {
        const val KEY_FIRST_LAUNCH = "key_first_launch"
        const val KEY_LAST_CURRENCY_UPDATE = "key_last_currency_update"
        const val KEY_APP_THEME = "key_app_theme"
        const val KEY_APP_LANGUAGE = "key_app_language"
        const val KEY_DEFAULT_CURRENCY_FROM = "key_default_currency_from"
        const val KEY_DEFAULT_CURRENCY_TO = "key_default_currency_to"
    }
}