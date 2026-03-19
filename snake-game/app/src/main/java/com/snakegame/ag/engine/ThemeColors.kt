package com.snakegame.ag.engine

import android.graphics.Color

data class ThemeColors(val bg: Int, val snake: Int, val head: Int, val food: Int, val grid: Int)

object Themes {
    val classic = ThemeColors(
        bg = Color.parseColor("#2E7D32"), snake = Color.parseColor("#1B5E20"),
        head = Color.parseColor("#0D3B0F"), food = Color.parseColor("#F44336"),
        grid = Color.parseColor("#2A6E2E")
    )
    val dark = ThemeColors(
        bg = Color.parseColor("#121212"), snake = Color.parseColor("#B0BEC5"),
        head = Color.parseColor("#ECEFF1"), food = Color.parseColor("#FF5722"),
        grid = Color.parseColor("#1E1E1E")
    )
    val neon = ThemeColors(
        bg = Color.parseColor("#0A0020"), snake = Color.parseColor("#00E676"),
        head = Color.parseColor("#69F0AE"), food = Color.parseColor("#FF1744"),
        grid = Color.parseColor("#1A0040")
    )

    fun get(name: String) = when (name) {
        "dark" -> dark; "neon" -> neon; else -> classic
    }
}
