package xyz.larkzhh.echomusic.compose.refresh

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.Animatable

class ProgressDrawable : PaintDrawable(), Animatable, ValueAnimator.AnimatorUpdateListener {

    private var mWidth = 0
    private var mHeight = 0
    private var mProgressDegree = 0
    private val mValueAnimator: ValueAnimator = ValueAnimator.ofInt(30, 3600).apply {
        duration = 10000
        interpolator = null
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART
    }
    private val mPath = Path()

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val value = animation.animatedValue as Int
        mProgressDegree = 30 * (value / 30)
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val width = bounds.width()
        val height = bounds.height()
        val r = maxOf(1f, width / 22f)

        if (mWidth != width || mHeight != height) {
            mPath.reset()
            mPath.addCircle(width - r, height / 2f, r, Path.Direction.CW)
            mPath.addRect(width - 5 * r, height / 2f - r, width - r, height / 2f + r, Path.Direction.CW)
            mPath.addCircle(width - 5 * r, height / 2f, r, Path.Direction.CW)
            mWidth = width
            mHeight = height
        }

        canvas.save()
        canvas.rotate(mProgressDegree.toFloat(), width / 2f, height / 2f)
        for (i in 0 until 12) {
            mPaint.alpha = (i + 5) * 0x11
            canvas.rotate(30f, width / 2f, height / 2f)
            canvas.drawPath(mPath, mPaint)
        }
        canvas.restore()
    }

    override fun start() {
        if (!mValueAnimator.isRunning) {
            mValueAnimator.addUpdateListener(this)
            mValueAnimator.start()
        }
    }

    override fun stop() {
        if (mValueAnimator.isRunning) {
            mValueAnimator.removeAllListeners()
            mValueAnimator.removeAllUpdateListeners()
            mValueAnimator.cancel()
        }
    }

    override fun isRunning(): Boolean = mValueAnimator.isRunning
}
