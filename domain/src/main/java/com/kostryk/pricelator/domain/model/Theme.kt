package com.kostryk.pricelator.domain.model

enum class Theme {
    SYSTEM_DEFAULT,
    LIGHT,
    DARK;

    companion object {
        fun fromString(value: String?): Theme {
            return when (value) {
                "light" -> LIGHT
                "dark" -> DARK
                "system" -> SYSTEM_DEFAULT
                else -> SYSTEM_DEFAULT
            }
        }
    }

    fun toStorageString(): String {
        return when (this) {
            SYSTEM_DEFAULT -> "system"
            LIGHT -> "light"
            DARK -> "dark"
        }
    }
}
