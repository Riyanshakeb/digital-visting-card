package com.azizgraphics.clcltr.ui.calculate

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.azizgraphics.clcltr.data.model.BannerItem

class BannerPreviewView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var items: List<BannerItem> = emptyList()

    private val gridPaint = Paint().apply {
        color = Color.parseColor("#E0E0E0")
        strokeWidth = 1f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(6f, 4f), 0f)
    }

    private val bannerPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint().apply {
        color = Color.parseColor("#1A73E8")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint().apply {
        color = Color.parseColor("#333333")
        textSize = 24f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val colors = listOf(
        Color.parseColor("#301A73E8"),
        Color.parseColor("#3000BFA5"),
        Color.parseColor("#30FF9800"),
        Color.parseColor("#30E91E63"),
        Color.parseColor("#309C27B0"),
        Color.parseColor("#30F44336")
    )

    fun setItems(bannerItems: List<BannerItem>) {
        items = bannerItems
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        if (items.isEmpty()) return

        val maxW = items.maxOfOrNull { it.widthInFeet } ?: 1.0
        val maxH = items.maxOfOrNull { it.heightInFeet } ?: 1.0
        val padding = 20f
        val availW = width - padding * 2
        val availH = height - padding * 2
        val scale = minOf(availW / maxW.toFloat(), availH / maxH.toFloat()) * 0.8f

        var offsetX = padding + 10f

        items.forEachIndexed { index, item ->
            val w = (item.widthInFeet * scale).toFloat()
            val h = (item.heightInFeet * scale).toFloat()

            if (w > 0 && h > 0) {
                val rect = RectF(offsetX, padding, offsetX + w, padding + h)

                bannerPaint.color = colors[index % colors.size]
                canvas.drawRoundRect(rect, 4f, 4f, bannerPaint)

                borderPaint.color = Color.parseColor("#1A73E8")
                canvas.drawRoundRect(rect, 4f, 4f, borderPaint)

                val label = "${formatDim(item.widthInFeet)}×${formatDim(item.heightInFeet)}"
                textPaint.textSize = minOf(w * 0.15f, 24f).coerceAtLeast(10f)
                canvas.drawText(label, rect.centerX(), rect.centerY() + textPaint.textSize / 3, textPaint)

                offsetX += w + 10f
            }
        }
    }

    private fun formatDim(feet: Double): String {
        val ft = feet.toInt()
        val inches = ((feet - ft) * 12).toInt()
        return if (inches > 0) "${ft}'${inches}\"" else "${ft}'"
    }

    private fun drawGrid(canvas: Canvas) {
        val step = 30f
        var x = 0f
        while (x < width) {
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
            x += step
        }
        var y = 0f
        while (y < height) {
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
            y += step
        }
    }
}
