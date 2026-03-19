package com.azizgraphics.clcltr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.azizgraphics.clcltr.databinding.ActivityMainBinding
import com.azizgraphics.clcltr.ui.calculate.CalculateFragment
import com.azizgraphics.clcltr.ui.history.HistoryFragment
import com.azizgraphics.clcltr.ui.settings.SettingsFragment
import com.azizgraphics.clcltr.util.Prefs

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) show(CalculateFragment())
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_calculate -> show(CalculateFragment())
                R.id.nav_history -> show(HistoryFragment())
                R.id.nav_settings -> show(SettingsFragment())
                else -> false
            }
        }
    }

    private fun show(f: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, f).commit()
        return true
    }

    private fun applyTheme() {
        when (Prefs(this).themeMode) {
            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}
