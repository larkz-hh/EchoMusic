package xyz.larkzhh.echomusic.compose.refresh

import android.graphics.Canvas
import android.graphics.Path

class ArrowDrawable : PaintDrawable() {

    private var mWidth = 0
    private var mHeight = 0
    private val mPath = Path()

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val width = bounds.width()
        val height = bounds.height()
        if (mWidth != width || mHeight != height) {
            val lineWidth = width * 30 / 225
            mPath.reset()

            val vector1 = lineWidth * 0.70710678118654752440084436210485f
            val vector2 = lineWidth / 0.70710678118654752440084436210485f
            mPath.moveTo(width / 2f, height.toFloat())
            mPath.lineTo(0f, height / 2f)
            mPath.lineTo(vector1, height / 2f - vector1)
            mPath.lineTo(width / 2f - lineWidth / 2f, height - vector2 - lineWidth / 2f)
            mPath.lineTo(width / 2f - lineWidth / 2f, 0f)
            mPath.lineTo(width / 2f + lineWidth / 2f, 0f)
            mPath.lineTo(width / 2f + lineWidth / 2f, height - vector2 - lineWidth / 2f)
            mPath.lineTo(width - vector1, height / 2f - vector1)
            mPath.lineTo(width.toFloat(), height / 2f)
            mPath.close()

            mWidth = width
            mHeight = height
        }
        canvas.drawPath(mPath, mPaint)
    }
}
