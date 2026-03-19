package com.snakegame.ag.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.snakegame.ag.data.GamePrefs
import kotlin.math.abs

class GameView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    companion object {
        const val GRID_SIZE = 20
    }

    private val prefs = GamePrefs(context)
    var state = GameState(GRID_SIZE, GRID_SIZE)
        private set
    var theme = Themes.get(prefs.theme)
        private set

    var onScoreChanged: ((Int) -> Unit)? = null
    var onGameOver: ((Int) -> Unit)? = null

    private val bgPaint = Paint()
    private val gridPaint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 0.5f }
    private val snakePaint = Paint().apply { isAntiAlias = true }
    private val headPaint = Paint().apply { isAntiAlias = true }
    private val foodPaint = Paint().apply { isAntiAlias = true }
    private val foodGlowPaint = Paint().apply { isAntiAlias = true; style = Paint.Style.FILL }

    private val handler = Handler(Looper.getMainLooper())
    private var cellSize = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    private var touchStartX = 0f
    private var touchStartY = 0f
    private var running = false

    private val gameLoop = object : Runnable {
        override fun run() {
            if (!running) return
            val wasOver = state.isOver
            state.update()
            if (!wasOver && state.isOver) {
                onGameOver?.invoke(state.score)
            } else if (!state.isOver) {
                onScoreChanged?.invoke(state.score)
            }
            invalidate()
            if (!state.isOver && !state.isPaused) {
                handler.postDelayed(this, state.delay)
            }
        }
    }

    fun startGame() {
        theme = Themes.get(prefs.theme)
        state.reset()
        running = true
        handler.removeCallbacks(gameLoop)
        handler.post(gameLoop)
        invalidate()
    }

    fun pauseGame() {
        state.isPaused = true
        running = false
        handler.removeCallbacks(gameLoop)
    }

    fun resumeGame() {
        if (state.isOver) return
        state.isPaused = false
        running = true
        handler.removeCallbacks(gameLoop)
        handler.post(gameLoop)
    }

    fun stopGame() {
        running = false
        handler.removeCallbacks(gameLoop)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cellSize = minOf(w.toFloat() / GRID_SIZE, h.toFloat() / GRID_SIZE)
        offsetX = (w - cellSize * GRID_SIZE) / 2f
        offsetY = (h - cellSize * GRID_SIZE) / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bgPaint.color = theme.bg
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        if (prefs.gridOn) {
            gridPaint.color = theme.grid
            for (i in 0..GRID_SIZE) {
                val x = offsetX + i * cellSize
                canvas.drawLine(x, offsetY, x, offsetY + GRID_SIZE * cellSize, gridPaint)
                val y = offsetY + i * cellSize
                canvas.drawLine(offsetX, y, offsetX + GRID_SIZE * cellSize, y, gridPaint)
            }
        }

        // Food glow
        foodGlowPaint.color = (theme.food and 0x00FFFFFF) or 0x30000000
        val fx = offsetX + state.food.x * cellSize + cellSize / 2
        val fy = offsetY + state.food.y * cellSize + cellSize / 2
        canvas.drawCircle(fx, fy, cellSize * 0.7f, foodGlowPaint)

        // Food
        foodPaint.color = theme.food
        canvas.drawCircle(fx, fy, cellSize * 0.4f, foodPaint)

        // Snake
        val r = cellSize * 0.42f
        state.snake.forEachIndexed { i, p ->
            val paint = if (i == 0) headPaint else snakePaint
            paint.color = if (i == 0) theme.head else theme.snake
            val cx = offsetX + p.x * cellSize + cellSize / 2
            val cy = offsetY + p.y * cellSize + cellSize / 2
            canvas.drawRoundRect(
                RectF(cx - r, cy - r, cx + r, cy + r),
                r * 0.4f, r * 0.4f, paint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.x; touchStartY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val dx = event.x - touchStartX
                val dy = event.y - touchStartY
                val minSwipe = 30f
                if (abs(dx) > abs(dy) && abs(dx) > minSwipe) {
                    state.setDir(if (dx > 0) Direction.RIGHT else Direction.LEFT)
                } else if (abs(dy) > abs(dx) && abs(dy) > minSwipe) {
                    state.setDir(if (dy > 0) Direction.DOWN else Direction.UP)
                }
            }
        }
        return true
    }
}
