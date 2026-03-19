package com.snakegame.ag.data

import android.content.Context

class GamePrefs(context: Context) {
    private val p = context.getSharedPreferences("snake_prefs", Context.MODE_PRIVATE)

    var highScore: Int
        get() = p.getInt("high_score", 0)
        set(v) = p.edit().putInt("high_score", v).apply()

    var soundOn: Boolean
        get() = p.getBoolean("sound", true)
        set(v) = p.edit().putBoolean("sound", v).apply()

    var vibrationOn: Boolean
        get() = p.getBoolean("vibration", true)
        set(v) = p.edit().putBoolean("vibration", v).apply()

    var gridOn: Boolean
        get() = p.getBoolean("grid", true)
        set(v) = p.edit().putBoolean("grid", v).apply()

    var theme: String
        get() = p.getString("theme", "classic") ?: "classic"
        set(v) = p.edit().putString("theme", v).apply()
}
