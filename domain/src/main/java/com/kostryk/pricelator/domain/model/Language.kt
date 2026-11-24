package com.kostryk.pricelator.domain.model

enum class Language(
    val code: String,
    val displayName: String
) {
    ENGLISH("en", "English"),
    GERMAN("de", "Deutsch"),
    FRENCH("fr", "Français"),
    SPANISH("es", "Español"),
    PORTUGUESE("pt", "Português"),
    ITALIAN("it", "Italiano");

    companion object {
        fun fromCode(code: String?): Language {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}
