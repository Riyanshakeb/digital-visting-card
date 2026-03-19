package com.azizgraphics.clcltr.util

import java.text.NumberFormat
import java.util.Locale

object Fmt {
    private val symbols = mapOf(
        "PKR" to "PKR", "USD" to "$", "EUR" to "€",
        "GBP" to "£", "INR" to "₹", "AED" to "AED", "SAR" to "SAR"
    )

    fun price(amount: Double, currency: String): String {
        val nf = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2; maximumFractionDigits = 2
        }
        return "${symbols[currency] ?: currency} ${nf.format(amount)}"
    }

    fun currencies(): List<String> = symbols.keys.toList()
}
