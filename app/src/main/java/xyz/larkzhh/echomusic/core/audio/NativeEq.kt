package xyz.larkzhh.echomusic.core.audio

/** native EQ，5 段 biquad 峰值滤波 + 输出增益 */
object NativeEq {

    val available: Boolean = runCatching { System.loadLibrary("echoeq") }.isSuccess

    fun init(sampleRate: Int, channels: Int) {
        if (available) nativeInit(sampleRate, channels)
    }

    fun setBand(index: Int, freq: Float, gainDb: Float, q: Float) {
        if (available) nativeSetBand(index, freq, gainDb, q)
    }

    fun setGain(gainDb: Float) {
        if (available) nativeSetGain(gainDb)
    }

    fun reset() {
        if (available) nativeReset()
    }

    fun process(buffer: ShortArray, length: Int) {
        if (available) nativeProcessShort(buffer, length)
    }

    private external fun nativeInit(sampleRate: Int, channels: Int)

    private external fun nativeSetBand(index: Int, freq: Float, gainDb: Float, q: Float)

    private external fun nativeSetGain(gainDb: Float)

    private external fun nativeReset()

    private external fun nativeProcessShort(buffer: ShortArray, length: Int)
}
