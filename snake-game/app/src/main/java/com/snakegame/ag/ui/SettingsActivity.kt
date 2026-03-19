package com.snakegame.ag.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.snakegame.ag.data.GamePrefs
import com.snakegame.ag.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var b: ActivitySettingsBinding
    private lateinit var prefs: GamePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        prefs = GamePrefs(this)

        b.swSound.isChecked = prefs.soundOn
        b.swVibration.isChecked = prefs.vibrationOn
        b.swGrid.isChecked = prefs.gridOn

        b.swSound.setOnCheckedChangeListener { _, on -> prefs.soundOn = on }
        b.swVibration.setOnCheckedChangeListener { _, on -> prefs.vibrationOn = on }
        b.swGrid.setOnCheckedChangeListener { _, on -> prefs.gridOn = on }

        b.btnClassic.setOnClickListener { prefs.theme = "classic"; highlightTheme("classic") }
        b.btnDark.setOnClickListener { prefs.theme = "dark"; highlightTheme("dark") }
        b.btnNeon.setOnClickListener { prefs.theme = "neon"; highlightTheme("neon") }

        b.btnBack.setOnClickListener { finish() }

        highlightTheme(prefs.theme)
    }

    private fun highlightTheme(t: String) {
        val alpha = 1.0f; val dim = 0.5f
        b.btnClassic.alpha = if (t == "classic") alpha else dim
        b.btnDark.alpha = if (t == "dark") alpha else dim
        b.btnNeon.alpha = if (t == "neon") alpha else dim
    }
}
