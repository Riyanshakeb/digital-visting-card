package com.snakegame.ag.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.snakegame.ag.data.GamePrefs
import com.snakegame.ag.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var b: ActivityMenuBinding
    private lateinit var prefs: GamePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(b.root)
        prefs = GamePrefs(this)
    }

    override fun onResume() {
        super.onResume()
        b.tvHighScore.text = "Best: ${prefs.highScore}"
    }

    override fun onStart() {
        super.onStart()
        b.btnPlay.setOnClickListener { startActivity(Intent(this, GameActivity::class.java)) }
        b.btnSettings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        b.btnExit.setOnClickListener { finishAffinity() }
    }
}
