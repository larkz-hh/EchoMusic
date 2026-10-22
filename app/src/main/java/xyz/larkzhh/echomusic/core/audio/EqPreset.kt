package xyz.larkzhh.echomusic.core.audio

/** 5 段 EQ 预设，频段固定为 60Hz / 230Hz / 910Hz / 3.6kHz / 14kHz */
enum class EqPreset(val title: String, val bands: FloatArray, val gainDb: Float) {
    OFF("关闭", floatArrayOf(0f, 0f, 0f, 0f, 0f), 0f),
    POP("流行", floatArrayOf(2f, 1f, 0f, 1.5f, 2f), 0f),
    ROCK("摇滚", floatArrayOf(4f, 2f, -1f, 2f, 3f), -1f),
    BASS("重低音", floatArrayOf(6f, 4f, 0f, 0f, 0f), -2f),
    VOCAL("人声", floatArrayOf(-2f, -1f, 2f, 3f, 1f), 0f),
}
