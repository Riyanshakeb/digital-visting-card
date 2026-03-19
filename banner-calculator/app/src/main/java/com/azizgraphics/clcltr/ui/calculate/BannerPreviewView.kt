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
        color = Color.parseColor("#D0D0D0"); strokeWidth = 1f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(5f, 4f), 0f)
    }
    private val fillPaint = Paint().apply { style = Paint.Style.FILL; isAntiAlias = true }
    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE; strokeWidth = 2f; isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        color = Color.parseColor("#333333"); textAlign = Paint.Align.CENTER; isAntiAlias = true
    }

    private val fills = intArrayOf(
        Color.parseColor("#302962FF"), Color.parseColor("#3010B981"),
        Color.parseColor("#30F59E0B"), Color.parseColor("#30EF4444"),
        Color.parseColor("#308B5CF6"), Color.parseColor("#30EC4899")
    )
    private val strokes = intArrayOf(
        Color.parseColor("#2962FF"), Color.parseColor("#10B981"),
        Color.parseColor("#F59E0B"), Color.parseColor("#EF4444"),
        Color.parseColor("#8B5CF6"), Color.parseColor("#EC4899")
    )

    fun updateItems(list: List<BannerItem>) { items = list; invalidate() }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        if (items.isEmpty()) return

        val validItems = items.filter { it.widthFt > 0 && it.heightFt > 0 }
        if (validItems.isEmpty()) return

        val maxDim = maxOf(
            validItems.maxOf { it.widthFt },
            validItems.maxOf { it.heightFt }
        )
        val pad = 24f
        val aW = width - pad * 2
        val aH = height - pad * 2
        val scale = minOf(aW, aH) / maxDim.toFloat() * 0.85f

        var ox = pad + 8f
        validItems.forEachIndexed { i, item ->
            val w = (item.widthFt * scale).toFloat()
            val h = (item.heightFt * scale).toFloat()
            val r = RectF(ox, pad, ox + w, pad + h)

            fillPaint.color = fills[i % fills.size]
            canvas.drawRoundRect(r, 6f, 6f, fillPaint)
            borderPaint.color = strokes[i % strokes.size]
            canvas.drawRoundRect(r, 6f, 6f, borderPaint)

            textPaint.textSize = minOf(w * 0.13f, 22f).coerceAtLeast(9f)
            canvas.drawText(item.dimensionLabel(), r.centerX(), r.centerY() + textPaint.textSize / 3, textPaint)
            ox += w + 8f
        }
    }

    private fun drawGrid(canvas: Canvas) {
        val step = 28f
        var x = 0f; while (x < width) { canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint); x += step }
        var y = 0f; while (y < height) { canvas.drawLine(0f, y, width.toFloat(), y, gridPaint); y += step }
    }
}
