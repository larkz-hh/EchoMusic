package xyz.larkzhh.echomusic.compose.refresh

import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

abstract class PaintDrawable : Drawable() {

    val mPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = 0xff666666.toInt()
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
