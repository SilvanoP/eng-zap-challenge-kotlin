package br.com.desafio.grupozap.utils

import androidx.core.text.isDigitsOnly

object Utils {

    @JvmStatic
    fun isValidNumber(number: String?): Boolean {
        return number?.isDigitsOnly() ?: false
    }
}