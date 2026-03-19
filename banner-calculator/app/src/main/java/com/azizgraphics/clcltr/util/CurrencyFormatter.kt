package com.azizgraphics.clcltr.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val currencySymbols = mapOf(
        "PKR" to "PKR",
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£",
        "INR" to "₹",
        "AED" to "AED",
        "SAR" to "SAR"
    )

    fun format(amount: Double, currency: String): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        val symbol = currencySymbols[currency] ?: currency
        return "$symbol ${formatter.format(amount)}"
    }

    fun getCurrencies(): List<String> = currencySymbols.keys.toList()
}
