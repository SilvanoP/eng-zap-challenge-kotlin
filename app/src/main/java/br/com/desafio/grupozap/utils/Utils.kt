package br.com.desafio.grupozap.utils

import androidx.core.text.isDigitsOnly

object Utils {

    @JvmStatic
    fun isValidNumber(number: String?): Boolean {
        return number?.isDigitsOnly() ?: false
    }

    fun fromDoubleToStringTwoDecimal(value: Double): String {
        return String.format("%.2f", value)
    }
}