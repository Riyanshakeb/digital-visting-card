package com.snakegame.ag.ui

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.snakegame.ag.data.GamePrefs
import com.snakegame.ag.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var b: ActivityGameBinding
    private lateinit var prefs: GamePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityGameBinding.inflate(layoutInflater)
        setContentView(b.root)
        prefs = GamePrefs(this)

        b.tvBest.text = "Best: ${prefs.highScore}"

        b.gameView.onScoreChanged = { score ->
            runOnUiThread { b.tvScore.text = "Score: $score" }
        }

        b.gameView.onGameOver = { score ->
            runOnUiThread { showGameOver(score) }
            if (prefs.vibrationOn) vibrate(100)
        }

        b.btnPause.setOnClickListener {
            b.gameView.pauseGame()
            b.pauseOverlay.visibility = View.VISIBLE
        }

        b.btnResume.setOnClickListener {
            b.pauseOverlay.visibility = View.GONE
            b.gameView.resumeGame()
        }

        b.btnMenuFromPause.setOnClickListener { finish() }

        b.btnRetry.setOnClickListener {
            b.gameOverOverlay.visibility = View.GONE
            b.tvScore.text = "Score: 0"
            b.tvBest.text = "Best: ${prefs.highScore}"
            b.gameView.startGame()
        }

        b.btnMenuFromOver.setOnClickListener { finish() }

        b.gameView.startGame()
    }

    private fun showGameOver(score: Int) {
        val isNewBest = score > prefs.highScore
        if (isNewBest) prefs.highScore = score
        b.tvFinalScore.text = score.toString()
        b.tvNewBest.visibility = if (isNewBest) View.VISIBLE else View.GONE
        b.tvGameOverBest.text = "Best: ${prefs.highScore}"
        b.gameOverOverlay.visibility = View.VISIBLE
    }

    @Suppress("DEPRECATION")
    private fun vibrate(ms: Long) {
        try {
            if (Build.VERSION.SDK_INT >= 31) {
                val vm = getSystemService(VibratorManager::class.java)
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                val v = getSystemService(VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= 26) {
                    v?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    v?.vibrate(ms)
                }
            }
        } catch (_: Exception) {}
    }

    override fun onPause() {
        super.onPause()
        if (!b.gameView.state.isOver) {
            b.gameView.pauseGame()
            b.pauseOverlay.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        b.gameView.stopGame()
    }
}
