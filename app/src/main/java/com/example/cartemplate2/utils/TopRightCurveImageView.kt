package com.example.cartemplate2.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class TopRightCurveImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val clipPath = Path()
    private val radius = 50f  // Adjust the radius as needed for the curve

    override fun onDraw(canvas: Canvas) {
        clipPath.reset()

        clipPath.addRoundRect(
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            floatArrayOf(0f, 0f, 0f, 0f, radius, radius, 0f, 0f),

            Path.Direction.CW
        )

        canvas.clipPath(clipPath)
        super.onDraw(canvas)
    }
}
