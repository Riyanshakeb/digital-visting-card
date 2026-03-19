package com.azizgraphics.clcltr.util

import android.content.Context

class Prefs(context: Context) {
    private val p = context.getSharedPreferences("bc_prefs", Context.MODE_PRIVATE)

    var defaultUnit: String
        get() = p.getString("unit", "Feet") ?: "Feet"
        set(v) = p.edit().putString("unit", v).apply()

    var defaultRate: Float
        get() = p.getFloat("rate", 0f)
        set(v) = p.edit().putFloat("rate", v).apply()

    var themeMode: String
        get() = p.getString("theme", "System") ?: "System"
        set(v) = p.edit().putString("theme", v).apply()

    var currency: String
        get() = p.getString("currency", "PKR") ?: "PKR"
        set(v) = p.edit().putString("currency", v).apply()

    var materials: Set<String>
        get() = p.getStringSet("materials", setOf("Vinyl", "Flex", "PVC", "Fabric", "Canvas")) ?: emptySet()
        set(v) = p.edit().putStringSet("materials", v).apply()
}
