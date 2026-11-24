package com.kostryk.pricelator.domain.util

import java.util.*

object CurrencyUtils {

    private var currentLocale: Locale = Locale.getDefault()

    private val currencyLocaleMap: SortedMap<Currency, Locale> by lazy {
        TreeMap<Currency, Locale> { c1, c2 ->
            c1.currencyCode.compareTo(c2.currencyCode)
        }.apply {
            Locale.getAvailableLocales().forEach { locale ->
                try {
                    val currency = Currency.getInstance(locale)
                    putIfAbsent(currency, locale)
                } catch (_: Exception) {
                }
            }
        }
    }

    fun setLocale(locale: Locale) {
        currentLocale = locale
    }

    fun getCountryFlag(currencyCode: String): String {
        return try {
            if (currencyCode.equals("eur", ignoreCase = true)) {
                return "\uD83C\uDDEA\uD83C\uDDFA"
            }

            val currency = Currency.getInstance(currencyCode)
            val locale = currencyLocaleMap[currency]
            val countryCode = locale?.country ?: return "🌐"

            if (countryCode.length != 2) return "🌐"

            val firstChar = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
            val secondChar = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6

            String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
        } catch (_: Exception) {
            "🌐"
        }
    }

    fun formatCurrencyName(currencyCode: String): String {
        return try {
            if (currencyCode.equals("eur", ignoreCase = true)) {
                val europeName = when (currentLocale.language) {
                    "de", "es", "pt", "it" -> "Europa"
                    else -> "Europe"
                }
                return "$currencyCode ($europeName)"
            }
            val currency = Currency.getInstance(currencyCode)
            val currencyLocale = currencyLocaleMap[currency]
            val countryName = currencyLocale?.getDisplayCountry(currentLocale) ?: currency.getDisplayName(currentLocale)
            "$currencyCode ($countryName)"
        } catch (_: Exception) {
            currencyCode
        }
    }
}
