package com.kostryk.pricelator.ui.util

import android.os.Bundle
import android.util.Base64
import androidx.navigation.NavType

object ByteArrayNavType : NavType<ByteArray>(isNullableAllowed = false) {

    override val name: String = "bytearray"

    override fun get(bundle: Bundle, key: String): ByteArray =
        bundle.getByteArray(key) ?: ByteArray(0)

    override fun parseValue(value: String): ByteArray =
        Base64.decode(value, Base64.URL_SAFE or Base64.NO_WRAP)

    override fun put(bundle: Bundle, key: String, value: ByteArray) {
        bundle.putByteArray(key, value)
    }
}
