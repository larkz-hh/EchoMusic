package xyz.larkzhh.echomusic.core.audio

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer

@UnstableApi
class NativeEqAudioProcessor : BaseAudioProcessor() {

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        NativeEq.init(inputAudioFormat.sampleRate, inputAudioFormat.channelCount)
        EqController.applyCurrent()
        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val size = inputBuffer.remaining()
        val output = replaceOutputBuffer(size)
        if (inputAudioFormat.encoding == C.ENCODING_PCM_16BIT) {
            val samples = ShortArray(size / 2)
            inputBuffer.asShortBuffer().get(samples)
            NativeEq.process(samples, samples.size)
            output.asShortBuffer().put(samples)
        } else {
            output.put(inputBuffer)
        }
        output.flip()
        inputBuffer.position(inputBuffer.limit())
    }

    override fun onReset() {
        NativeEq.reset()
    }
}
