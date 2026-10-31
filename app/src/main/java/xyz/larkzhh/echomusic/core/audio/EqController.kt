package xyz.larkzhh.echomusic.core.audio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tencent.mmkv.MMKV

object EqController {

    private val freqs = floatArrayOf(60f, 230f, 910f, 3600f, 14000f)
    private const val KEY = "eq_preset"
    private const val Q = 1.2f

    var preset by mutableStateOf(EqPreset.OFF)
        private set

    init {
        val saved = MMKV.defaultMMKV().decodeString(KEY)
        preset = runCatching { EqPreset.valueOf(saved ?: EqPreset.OFF.name) }
            .getOrDefault(EqPreset.OFF)
    }

    fun apply(target: EqPreset) {
        preset = target
        MMKV.defaultMMKV().encode(KEY, target.name)
        applyCurrent()
    }

    fun applyCurrent() {
        preset.bands.forEachIndexed { index, gain ->
            NativeEq.setBand(index, freqs[index], gain, Q)
        }
        NativeEq.setGain(preset.gainDb)
    }
}
